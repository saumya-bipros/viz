package com.vizzionnaire.server.common.transport.service;

import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import com.vizzionnaire.server.queue.kafka.TbKafkaEncoder;

/**
 * Created by ashvayka on 05.10.18.
 */
public class ToRuleEngineMsgEncoder implements TbKafkaEncoder<ToRuleEngineMsg> {
    @Override
    public byte[] encode(ToRuleEngineMsg value) {
        return value.toByteArray();
    }
}
