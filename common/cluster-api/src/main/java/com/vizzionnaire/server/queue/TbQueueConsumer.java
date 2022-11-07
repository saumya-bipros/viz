package com.vizzionnaire.server.queue;

import java.util.List;
import java.util.Set;

import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;

public interface TbQueueConsumer<T extends TbQueueMsg> {

    String getTopic();

    void subscribe();

    void subscribe(Set<TopicPartitionInfo> partitions);

    void unsubscribe();

    List<T> poll(long durationInMillis);

    void commit();

    boolean isStopped();

}
