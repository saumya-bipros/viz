package com.vizzionnaire.server.service.queue.processing;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.msg.gen.MsgProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class SequentialByOriginatorIdTbRuleEngineSubmitStrategy extends SequentialByEntityIdTbRuleEngineSubmitStrategy {

    public SequentialByOriginatorIdTbRuleEngineSubmitStrategy(String queueName) {
        super(queueName);
    }

    @Override
    protected EntityId getEntityId(TransportProtos.ToRuleEngineMsg msg) {
        try {
            MsgProtos.TbMsgProto proto = MsgProtos.TbMsgProto.parseFrom(msg.getTbMsg());
            return EntityIdFactory.getByTypeAndUuid(proto.getEntityType(), new UUID(proto.getEntityIdMSB(), proto.getEntityIdLSB()));
        } catch (InvalidProtocolBufferException e) {
            log.warn("[{}] Failed to parse TbMsg: {}", queueName, msg);
            return null;
        }
    }
}
