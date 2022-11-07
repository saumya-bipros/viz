package com.vizzionnaire.server.queue.discovery.event;

import lombok.Getter;

import java.util.Set;

import com.vizzionnaire.server.queue.discovery.QueueKey;

public class ClusterTopologyChangeEvent extends TbApplicationEvent {

    private static final long serialVersionUID = -2441739930040282254L;

    @Getter
    private final Set<QueueKey> queueKeys;

    public ClusterTopologyChangeEvent(Object source, Set<QueueKey> queueKeys) {
        super(source);
        this.queueKeys = queueKeys;
    }
}
