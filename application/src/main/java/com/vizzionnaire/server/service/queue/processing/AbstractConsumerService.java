package com.vizzionnaire.server.service.queue.processing;

import com.google.protobuf.ByteString;
import com.vizzionnaire.common.util.VizzionnaireThreadFactory;
import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.TenantProfileId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.common.msg.TbActorMsg;
import com.vizzionnaire.server.common.msg.plugin.ComponentLifecycleMsg;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.discovery.PartitionService;
import com.vizzionnaire.server.queue.discovery.TbApplicationEventListener;
import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;
import com.vizzionnaire.server.queue.util.AfterStartUp;
import com.vizzionnaire.server.queue.util.DataDecodingEncodingService;
import com.vizzionnaire.server.service.apiusage.TbApiUsageStateService;
import com.vizzionnaire.server.service.profile.TbDeviceProfileCache;
import com.vizzionnaire.server.service.queue.TbPackCallback;
import com.vizzionnaire.server.service.queue.TbPackProcessingContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractConsumerService<N extends com.google.protobuf.GeneratedMessageV3> extends TbApplicationEventListener<PartitionChangeEvent> {

    protected volatile ExecutorService consumersExecutor;
    protected volatile ExecutorService notificationsConsumerExecutor;
    protected volatile boolean stopped = false;

    protected final ActorSystemContext actorContext;
    protected final DataDecodingEncodingService encodingService;
    protected final TbTenantProfileCache tenantProfileCache;
    protected final TbDeviceProfileCache deviceProfileCache;
    protected final TbApiUsageStateService apiUsageStateService;
    protected final PartitionService partitionService;

    protected final TbQueueConsumer<TbProtoQueueMsg<N>> nfConsumer;

    public AbstractConsumerService(ActorSystemContext actorContext, DataDecodingEncodingService encodingService,
                                   TbTenantProfileCache tenantProfileCache, TbDeviceProfileCache deviceProfileCache,
                                   TbApiUsageStateService apiUsageStateService, PartitionService partitionService,
                                   TbQueueConsumer<TbProtoQueueMsg<N>> nfConsumer) {
        this.actorContext = actorContext;
        this.encodingService = encodingService;
        this.tenantProfileCache = tenantProfileCache;
        this.deviceProfileCache = deviceProfileCache;
        this.apiUsageStateService = apiUsageStateService;
        this.partitionService = partitionService;
        this.nfConsumer = nfConsumer;
    }

    public void init(String mainConsumerThreadName, String nfConsumerThreadName) {
        this.consumersExecutor = Executors.newCachedThreadPool(VizzionnaireThreadFactory.forName(mainConsumerThreadName));
        this.notificationsConsumerExecutor = Executors.newSingleThreadExecutor(VizzionnaireThreadFactory.forName(nfConsumerThreadName));
    }

    @AfterStartUp(order = AfterStartUp.REGULAR_SERVICE)
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("Subscribing to notifications: {}", nfConsumer.getTopic());
        this.nfConsumer.subscribe();
        launchNotificationsConsumer();
        launchMainConsumers();
    }

    protected abstract ServiceType getServiceType();

    protected abstract void launchMainConsumers();

    protected abstract void stopMainConsumers();

    protected abstract long getNotificationPollDuration();

    protected abstract long getNotificationPackProcessingTimeout();

    protected void launchNotificationsConsumer() {
        notificationsConsumerExecutor.submit(() -> {
            while (!stopped) {
                try {
                    List<TbProtoQueueMsg<N>> msgs = nfConsumer.poll(getNotificationPollDuration());
                    if (msgs.isEmpty()) {
                        continue;
                    }
                    ConcurrentMap<UUID, TbProtoQueueMsg<N>> pendingMap = msgs.stream().collect(
                            Collectors.toConcurrentMap(s -> UUID.randomUUID(), Function.identity()));
                    CountDownLatch processingTimeoutLatch = new CountDownLatch(1);
                    TbPackProcessingContext<TbProtoQueueMsg<N>> ctx = new TbPackProcessingContext<>(
                            processingTimeoutLatch, pendingMap, new ConcurrentHashMap<>());
                    pendingMap.forEach((id, msg) -> {
                        log.trace("[{}] Creating notification callback for message: {}", id, msg.getValue());
                        TbCallback callback = new TbPackCallback<>(id, ctx);
                        try {
                            handleNotification(id, msg, callback);
                        } catch (Throwable e) {
                            log.warn("[{}] Failed to process notification: {}", id, msg, e);
                            callback.onFailure(e);
                        }
                    });
                    if (!processingTimeoutLatch.await(getNotificationPackProcessingTimeout(), TimeUnit.MILLISECONDS)) {
                        ctx.getAckMap().forEach((id, msg) -> log.warn("[{}] Timeout to process notification: {}", id, msg.getValue()));
                        ctx.getFailedMap().forEach((id, msg) -> log.warn("[{}] Failed to process notification: {}", id, msg.getValue()));
                    }
                    nfConsumer.commit();
                } catch (Exception e) {
                    if (!stopped) {
                        log.warn("Failed to obtain notifications from queue.", e);
                        try {
                            Thread.sleep(getNotificationPollDuration());
                        } catch (InterruptedException e2) {
                            log.trace("Failed to wait until the server has capacity to handle new notifications", e2);
                        }
                    }
                }
            }
            log.info("TB Notifications Consumer stopped.");
        });
    }

    protected void handleComponentLifecycleMsg(UUID id, ByteString nfMsg) {
        Optional<TbActorMsg> actorMsgOpt = encodingService.decode(nfMsg.toByteArray());
        if (actorMsgOpt.isPresent()) {
            TbActorMsg actorMsg = actorMsgOpt.get();
            if (actorMsg instanceof ComponentLifecycleMsg) {
                ComponentLifecycleMsg componentLifecycleMsg = (ComponentLifecycleMsg) actorMsg;
                log.debug("[{}][{}][{}] Received Lifecycle event: {}", componentLifecycleMsg.getTenantId(), componentLifecycleMsg.getEntityId().getEntityType(),
                        componentLifecycleMsg.getEntityId(), componentLifecycleMsg.getEvent());
                if (EntityType.TENANT_PROFILE.equals(componentLifecycleMsg.getEntityId().getEntityType())) {
                    TenantProfileId tenantProfileId = new TenantProfileId(componentLifecycleMsg.getEntityId().getId());
                    tenantProfileCache.evict(tenantProfileId);
                    if (componentLifecycleMsg.getEvent().equals(ComponentLifecycleEvent.UPDATED)) {
                        apiUsageStateService.onTenantProfileUpdate(tenantProfileId);
                    }
                } else if (EntityType.TENANT.equals(componentLifecycleMsg.getEntityId().getEntityType())) {
                    tenantProfileCache.evict(componentLifecycleMsg.getTenantId());
                    partitionService.removeTenant(componentLifecycleMsg.getTenantId());
                    if (componentLifecycleMsg.getEvent().equals(ComponentLifecycleEvent.UPDATED)) {
                        apiUsageStateService.onTenantUpdate(componentLifecycleMsg.getTenantId());
                    } else if (componentLifecycleMsg.getEvent().equals(ComponentLifecycleEvent.DELETED)) {
                        apiUsageStateService.onTenantDelete((TenantId) componentLifecycleMsg.getEntityId());
                    }
                } else if (EntityType.DEVICE_PROFILE.equals(componentLifecycleMsg.getEntityId().getEntityType())) {
                    deviceProfileCache.evict(componentLifecycleMsg.getTenantId(), new DeviceProfileId(componentLifecycleMsg.getEntityId().getId()));
                } else if (EntityType.DEVICE.equals(componentLifecycleMsg.getEntityId().getEntityType())) {
                    deviceProfileCache.evict(componentLifecycleMsg.getTenantId(), new DeviceId(componentLifecycleMsg.getEntityId().getId()));
                } else if (EntityType.ENTITY_VIEW.equals(componentLifecycleMsg.getEntityId().getEntityType())) {
                    actorContext.getTbEntityViewService().onComponentLifecycleMsg(componentLifecycleMsg);
                } else if (EntityType.API_USAGE_STATE.equals(componentLifecycleMsg.getEntityId().getEntityType())) {
                    apiUsageStateService.onApiUsageStateUpdate(componentLifecycleMsg.getTenantId());
                } else if (EntityType.CUSTOMER.equals(componentLifecycleMsg.getEntityId().getEntityType())) {
                    if (componentLifecycleMsg.getEvent() == ComponentLifecycleEvent.DELETED) {
                        apiUsageStateService.onCustomerDelete((CustomerId) componentLifecycleMsg.getEntityId());
                    }
                }
            }
            log.trace("[{}] Forwarding message to App Actor {}", id, actorMsg);
            actorContext.tellWithHighPriority(actorMsg);
        }
    }

    protected abstract void handleNotification(UUID id, TbProtoQueueMsg<N> msg, TbCallback callback) throws Exception;

    @PreDestroy
    public void destroy() {
        stopped = true;
        stopMainConsumers();
        if (nfConsumer != null) {
            nfConsumer.unsubscribe();
        }
        if (consumersExecutor != null) {
            consumersExecutor.shutdownNow();
        }
        if (notificationsConsumerExecutor != null) {
            notificationsConsumerExecutor.shutdownNow();
        }
    }
}
