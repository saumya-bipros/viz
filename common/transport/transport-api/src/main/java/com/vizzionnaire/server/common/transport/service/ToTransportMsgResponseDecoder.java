package com.vizzionnaire.server.common.transport.service;

import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportMsg;
import com.vizzionnaire.server.queue.TbQueueMsg;
import com.vizzionnaire.server.queue.kafka.TbKafkaDecoder;

import java.io.IOException;

/**
 * Created by ashvayka on 05.10.18.
 */
public class ToTransportMsgResponseDecoder implements TbKafkaDecoder<ToTransportMsg> {

    @Override
    public ToTransportMsg decode(TbQueueMsg msg) throws IOException {
        return ToTransportMsg.parseFrom(msg.getData());
    }
}
