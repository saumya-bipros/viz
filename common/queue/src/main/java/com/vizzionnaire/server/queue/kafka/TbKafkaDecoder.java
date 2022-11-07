package com.vizzionnaire.server.queue.kafka;

import java.io.IOException;

import com.vizzionnaire.server.queue.TbQueueMsg;

/**
 * Created by ashvayka on 25.09.18.
 */
public interface TbKafkaDecoder<T> {

    T decode(TbQueueMsg msg) throws IOException;

}
