package com.vizzionnaire.server.actors.stats;

import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;

public final class StatsPersistTick implements TbActorMsg {
    @Override
    public MsgType getMsgType() {
        return MsgType.STATS_PERSIST_TICK_MSG;
    }
}
