package com.vizzionnaire.server.common.transport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.common.util.ThingsBoardExecutors;
import com.vizzionnaire.server.cache.ota.OtaPackageDataCache;
import com.vizzionnaire.server.common.transport.limits.TransportRateLimitService;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.scheduler.SchedulerComponent;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;

/**
 * Created by ashvayka on 15.10.18.
 */
@Slf4j
@Data
public abstract class TransportContext {

    protected final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected TransportService transportService;

    @Autowired
    private TbServiceInfoProvider serviceInfoProvider;

    @Autowired
    private SchedulerComponent scheduler;

    @Getter
    private ExecutorService executor;

    @Getter
    @Autowired
    private OtaPackageDataCache otaPackageDataCache;

    @Autowired
    private TransportResourceCache transportResourceCache;

    @Autowired
    protected TransportRateLimitService rateLimitService;

    @PostConstruct
    public void init() {
        executor = ThingsBoardExecutors.newWorkStealingPool(50, getClass());
    }

    @PreDestroy
    public void stop() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public String getNodeId() {
        return serviceInfoProvider.getServiceId();
    }



}
