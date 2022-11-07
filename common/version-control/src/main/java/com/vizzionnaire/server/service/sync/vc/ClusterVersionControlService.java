package com.vizzionnaire.server.service.sync.vc;

import org.springframework.context.ApplicationListener;

import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;

public interface ClusterVersionControlService extends ApplicationListener<PartitionChangeEvent> {
}
