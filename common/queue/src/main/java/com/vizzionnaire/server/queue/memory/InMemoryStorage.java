package com.vizzionnaire.server.queue.memory;

import java.util.List;

import com.vizzionnaire.server.queue.TbQueueMsg;

public interface InMemoryStorage {

    void printStats();

    int getLagTotal();

    boolean put(String topic, TbQueueMsg msg);

    <T extends TbQueueMsg> List<T> get(String topic) throws InterruptedException;

}
