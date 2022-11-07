package com.vizzionnaire.server.common.transport.service;

import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import com.vizzionnaire.server.queue.kafka.TbKafkaEncoder;

/**
 * Created by ashvayka on 05.10.18.
 */
public class TransportApiRequestEncoder implements TbKafkaEncoder<TransportApiRequestMsg> {
    @Override
    public byte[] encode(TransportApiRequestMsg value) {
        return value.toByteArray();
    }
}
