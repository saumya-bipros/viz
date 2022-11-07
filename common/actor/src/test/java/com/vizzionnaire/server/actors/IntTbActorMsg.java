package com.vizzionnaire.server.actors;

import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;

import lombok.Getter;

public class IntTbActorMsg implements TbActorMsg {

    @Getter
    private final int value;

    public IntTbActorMsg(int value) {
        this.value = value;
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.QUEUE_TO_RULE_ENGINE_MSG;
    }
}
