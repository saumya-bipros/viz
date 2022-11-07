package com.vizzionnaire.server.common.transport.service;

import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import com.vizzionnaire.server.queue.TbQueueMsg;
import com.vizzionnaire.server.queue.kafka.TbKafkaDecoder;

import java.io.IOException;

/**
 * Created by ashvayka on 05.10.18.
 */
public class TransportApiResponseDecoder implements TbKafkaDecoder<TransportApiResponseMsg> {

    @Override
    public TransportApiResponseMsg decode(TbQueueMsg msg) throws IOException {
        return TransportApiResponseMsg.parseFrom(msg.getData());
    }
}
