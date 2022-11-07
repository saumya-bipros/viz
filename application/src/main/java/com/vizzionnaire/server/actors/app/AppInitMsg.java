package com.vizzionnaire.server.actors.app;

import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;

public class AppInitMsg implements TbActorMsg {

    @Override
    public MsgType getMsgType() {
        return MsgType.APP_INIT_MSG;
    }
}
