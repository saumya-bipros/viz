package com.vizzionnaire.server.service.queue.processing;

import lombok.Getter;

import java.util.UUID;

import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;

public class IdMsgPair<T extends com.google.protobuf.GeneratedMessageV3> {
    @Getter
    final UUID uuid;
    @Getter
    final TbProtoQueueMsg<T> msg;

    public IdMsgPair(UUID uuid, TbProtoQueueMsg<T> msg) {
        this.uuid = uuid;
        this.msg = msg;
    }
}
