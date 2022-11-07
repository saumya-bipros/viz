package com.vizzionnaire.server.service.queue;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;

import java.util.Collections;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Data
public class TbTopicWithConsumerPerPartition {
    private final String topic;
    @Getter
    private final ReentrantLock lock = new ReentrantLock(); //NonfairSync
    private volatile Set<TopicPartitionInfo> partitions = Collections.emptySet();
    private final ConcurrentMap<TopicPartitionInfo, TbQueueConsumer<TbProtoQueueMsg<TransportProtos.ToRuleEngineMsg>>> consumers = new ConcurrentHashMap<>();
    private final Queue<Set<TopicPartitionInfo>> subscribeQueue = new ConcurrentLinkedQueue<>();
}
