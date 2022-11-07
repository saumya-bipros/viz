package com.vizzionnaire.server.actors.device;

import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;

/**
 * Created by ashvayka on 29.10.18.
 */
public class SessionTimeoutCheckMsg implements TbActorMsg {

    private static final SessionTimeoutCheckMsg INSTANCE = new SessionTimeoutCheckMsg();

    private SessionTimeoutCheckMsg() {
    }

    public static SessionTimeoutCheckMsg instance() {
        return INSTANCE;
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.SESSION_TIMEOUT_MSG;
    }
}
