package com.vizzionnaire.server.actors.device;

import com.vizzionnaire.server.gen.transport.TransportProtos.SessionType;

import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
class SessionInfoMetaData {
    private final SessionInfo sessionInfo;
    private long lastActivityTime;
    private boolean subscribedToAttributes;
    private boolean subscribedToRPC;

    SessionInfoMetaData(SessionInfo sessionInfo) {
        this(sessionInfo, System.currentTimeMillis());
    }

    SessionInfoMetaData(SessionInfo sessionInfo, long lastActivityTime) {
        this.sessionInfo = sessionInfo;
        this.lastActivityTime = lastActivityTime;
    }
}
