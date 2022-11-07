package com.vizzionnaire.server.vc.service;

import org.springframework.stereotype.Service;

import com.vizzionnaire.server.queue.discovery.QueueRoutingInfo;
import com.vizzionnaire.server.queue.discovery.QueueRoutingInfoService;

import java.util.Collections;
import java.util.List;

@Service
public class VersionControlQueueRoutingInfoService implements QueueRoutingInfoService {
    @Override
    public List<QueueRoutingInfo> getAllQueuesRoutingInfo() {
        return Collections.emptyList();
    }
}
