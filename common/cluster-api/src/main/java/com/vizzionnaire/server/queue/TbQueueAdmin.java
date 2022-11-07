package com.vizzionnaire.server.queue;

public interface TbQueueAdmin {

    void createTopicIfNotExists(String topic);

    void destroy();

    void deleteTopic(String topic);
}
