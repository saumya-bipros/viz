package com.vizzionnaire.server.actors.device;

import com.vizzionnaire.server.gen.transport.TransportProtos.SessionType;

import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
public class SessionInfo {
    private final SessionType type;
    private final String nodeId;
}
