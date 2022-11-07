package com.vizzionnaire.server.service.telemetry;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.common.util.ThingsBoardThreadFactory;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;
import com.vizzionnaire.server.queue.discovery.PartitionService;
import com.vizzionnaire.server.queue.discovery.TbApplicationEventListener;
import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;
import com.vizzionnaire.server.service.subscription.SubscriptionManagerService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by ashvayka on 27.03.18.
 */
@Slf4j
public abstract class AbstractSubscriptionService extends TbApplicationEventListener<PartitionChangeEvent>{

    protected final Set<TopicPartitionInfo> currentPartitions = ConcurrentHashMap.newKeySet();

    protected final TbClusterService clusterService;
    protected final PartitionService partitionService;
    protected Optional<SubscriptionManagerService> subscriptionManagerService;

    protected ExecutorService wsCallBackExecutor;

    public AbstractSubscriptionService(TbClusterService clusterService,
                                       PartitionService partitionService) {
        this.clusterService = clusterService;
        this.partitionService = partitionService;
    }

    @Autowired(required = false)
    public void setSubscriptionManagerService(Optional<SubscriptionManagerService> subscriptionManagerService) {
        this.subscriptionManagerService = subscriptionManagerService;
    }

    abstract String getExecutorPrefix();

    @PostConstruct
    public void initExecutor() {
        wsCallBackExecutor = Executors.newSingleThreadExecutor(ThingsBoardThreadFactory.forName(getExecutorPrefix() + "-service-ws-callback"));
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (wsCallBackExecutor != null) {
            wsCallBackExecutor.shutdownNow();
        }
    }

    @Override
    protected void onTbApplicationEvent(PartitionChangeEvent partitionChangeEvent) {
        if (ServiceType.TB_CORE.equals(partitionChangeEvent.getServiceType())) {
            currentPartitions.clear();
            currentPartitions.addAll(partitionChangeEvent.getPartitions());
        }
    }

    protected <T> void addWsCallback(ListenableFuture<T> saveFuture, Consumer<T> callback) {
        Futures.addCallback(saveFuture, new FutureCallback<T>() {
            @Override
            public void onSuccess(@Nullable T result) {
                callback.accept(result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        }, wsCallBackExecutor);
    }
}
