package com.vizzionnaire.server.service.subscription;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.vizzionnaire.common.util.VizzionnaireExecutors;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.discovery.PartitionService;
import com.vizzionnaire.server.queue.discovery.TbApplicationEventListener;
import com.vizzionnaire.server.queue.discovery.event.ClusterTopologyChangeEvent;
import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.telemetry.sub.AlarmSubscriptionUpdate;
import com.vizzionnaire.server.service.telemetry.sub.TelemetrySubscriptionUpdate;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Slf4j
@TbCoreComponent
@Service
public class DefaultTbLocalSubscriptionService implements TbLocalSubscriptionService {

    private final Set<TopicPartitionInfo> currentPartitions = ConcurrentHashMap.newKeySet();
    private final Map<String, Map<Integer, TbSubscription>> subscriptionsBySessionId = new ConcurrentHashMap<>();

    @Autowired
    private PartitionService partitionService;

    @Autowired
    private TbClusterService clusterService;

    @Autowired
    @Lazy
    private SubscriptionManagerService subscriptionManagerService;

    private ExecutorService subscriptionUpdateExecutor;

    private TbApplicationEventListener<PartitionChangeEvent> partitionChangeListener = new TbApplicationEventListener<>() {
        @Override
        protected void onTbApplicationEvent(PartitionChangeEvent event) {
            if (ServiceType.TB_CORE.equals(event.getServiceType())) {
                currentPartitions.clear();
                currentPartitions.addAll(event.getPartitions());
            }
        }
    };

    private TbApplicationEventListener<ClusterTopologyChangeEvent> clusterTopologyChangeListener = new TbApplicationEventListener<>() {
        @Override
        protected void onTbApplicationEvent(ClusterTopologyChangeEvent event) {
            if (event.getQueueKeys().stream().anyMatch(key -> ServiceType.TB_CORE.equals(key.getType()))) {
                /*
                 * If the cluster topology has changed, we need to push all current subscriptions to SubscriptionManagerService again.
                 * Otherwise, the SubscriptionManagerService may "forget" those subscriptions in case of restart.
                 * Although this is resource consuming operation, it is cheaper than sending ping/pong commands periodically
                 * It is also cheaper then caching the subscriptions by entity id and then lookup of those caches every time we have new telemetry in SubscriptionManagerService.
                 * Even if we cache locally the list of active subscriptions by entity id, it is still time consuming operation to get them from cache
                 * Since number of subscriptions is usually much less then number of devices that are pushing data.
                 */
                subscriptionsBySessionId.values().forEach(map -> map.values()
                        .forEach(sub -> pushSubscriptionToManagerService(sub, true)));
            }
        }
    };

    @PostConstruct
    public void initExecutor() {
        subscriptionUpdateExecutor = VizzionnaireExecutors.newWorkStealingPool(20, getClass());
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (subscriptionUpdateExecutor != null) {
            subscriptionUpdateExecutor.shutdownNow();
        }
    }

    @Override
    @EventListener(PartitionChangeEvent.class)
    public void onApplicationEvent(PartitionChangeEvent event) {
        partitionChangeListener.onApplicationEvent(event);
    }

    @Override
    @EventListener(ClusterTopologyChangeEvent.class)
    public void onApplicationEvent(ClusterTopologyChangeEvent event) {
        clusterTopologyChangeListener.onApplicationEvent(event);
    }

    //TODO 3.1: replace null callbacks with callbacks from websocket service.
    @Override
    public void addSubscription(TbSubscription subscription) {
        pushSubscriptionToManagerService(subscription, true);
        registerSubscription(subscription);
    }

    private void pushSubscriptionToManagerService(TbSubscription subscription, boolean pushToLocalService) {
        TopicPartitionInfo tpi = partitionService.resolve(ServiceType.TB_CORE, subscription.getTenantId(), subscription.getEntityId());
        if (currentPartitions.contains(tpi)) {
            // Subscription is managed on the same server;
            if (pushToLocalService) {
                subscriptionManagerService.addSubscription(subscription, TbCallback.EMPTY);
            }
        } else {
            // Push to the queue;
            TransportProtos.ToCoreMsg toCoreMsg = TbSubscriptionUtils.toNewSubscriptionProto(subscription);
            clusterService.pushMsgToCore(tpi, subscription.getEntityId().getId(), toCoreMsg, null);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSubscriptionUpdate(String sessionId, TelemetrySubscriptionUpdate update, TbCallback callback) {
        TbSubscription subscription = subscriptionsBySessionId
                .getOrDefault(sessionId, Collections.emptyMap()).get(update.getSubscriptionId());
        if (subscription != null) {
            switch (subscription.getType()) {
                case TIMESERIES:
                    TbTimeseriesSubscription tsSub = (TbTimeseriesSubscription) subscription;
                    update.getLatestValues().forEach((key, value) -> tsSub.getKeyStates().put(key, value));
                    break;
                case ATTRIBUTES:
                    TbAttributeSubscription attrSub = (TbAttributeSubscription) subscription;
                    update.getLatestValues().forEach((key, value) -> attrSub.getKeyStates().put(key, value));
                    break;
            }
            subscriptionUpdateExecutor.submit(() -> subscription.getUpdateConsumer().accept(sessionId, update));
        }
        callback.onSuccess();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSubscriptionUpdate(String sessionId, AlarmSubscriptionUpdate update, TbCallback callback) {
        TbSubscription subscription = subscriptionsBySessionId
                .getOrDefault(sessionId, Collections.emptyMap()).get(update.getSubscriptionId());
        if (subscription != null && subscription.getType() == TbSubscriptionType.ALARMS) {
            subscriptionUpdateExecutor.submit(() -> subscription.getUpdateConsumer().accept(sessionId, update));
        }
        callback.onSuccess();
    }

    @Override
    public void cancelSubscription(String sessionId, int subscriptionId) {
        log.debug("[{}][{}] Going to remove subscription.", sessionId, subscriptionId);
        Map<Integer, TbSubscription> sessionSubscriptions = subscriptionsBySessionId.get(sessionId);
        if (sessionSubscriptions != null) {
            TbSubscription subscription = sessionSubscriptions.remove(subscriptionId);
            if (subscription != null) {
                if (sessionSubscriptions.isEmpty()) {
                    subscriptionsBySessionId.remove(sessionId);
                }
                TopicPartitionInfo tpi = partitionService.resolve(ServiceType.TB_CORE, subscription.getTenantId(), subscription.getEntityId());
                if (currentPartitions.contains(tpi)) {
                    // Subscription is managed on the same server;
                    subscriptionManagerService.cancelSubscription(sessionId, subscriptionId, TbCallback.EMPTY);
                } else {
                    // Push to the queue;
                    TransportProtos.ToCoreMsg toCoreMsg = TbSubscriptionUtils.toCloseSubscriptionProto(subscription);
                    clusterService.pushMsgToCore(tpi, subscription.getEntityId().getId(), toCoreMsg, null);
                }
            } else {
                log.debug("[{}][{}] Subscription not found!", sessionId, subscriptionId);
            }
        } else {
            log.debug("[{}] No session subscriptions found!", sessionId);
        }
    }

    @Override
    public void cancelAllSessionSubscriptions(String sessionId) {
        Map<Integer, TbSubscription> subscriptions = subscriptionsBySessionId.get(sessionId);
        if (subscriptions != null) {
            Set<Integer> toRemove = new HashSet<>(subscriptions.keySet());
            toRemove.forEach(id -> cancelSubscription(sessionId, id));
        }
    }

    private void registerSubscription(TbSubscription subscription) {
        Map<Integer, TbSubscription> sessionSubscriptions = subscriptionsBySessionId.computeIfAbsent(subscription.getSessionId(), k -> new ConcurrentHashMap<>());
        sessionSubscriptions.put(subscription.getSubscriptionId(), subscription);
    }

}
