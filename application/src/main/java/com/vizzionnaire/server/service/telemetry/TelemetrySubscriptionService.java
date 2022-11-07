package com.vizzionnaire.server.service.telemetry;

import org.springframework.context.ApplicationListener;

import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;

/**
 * Created by ashvayka on 27.03.18.
 */
public interface TelemetrySubscriptionService extends InternalTelemetryService, ApplicationListener<PartitionChangeEvent> {

}
