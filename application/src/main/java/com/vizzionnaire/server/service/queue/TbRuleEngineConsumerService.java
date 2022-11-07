package com.vizzionnaire.server.service.queue;

import org.springframework.context.ApplicationListener;

import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;

public interface TbRuleEngineConsumerService extends ApplicationListener<PartitionChangeEvent> {

}
