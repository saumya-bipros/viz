package com.vizzionnaire.server.actors.device;

import lombok.Data;

import java.util.UUID;

import com.vizzionnaire.server.gen.transport.TransportProtos;

/**
 * @author Andrew Shvayka
 */
@Data
public class ToServerRpcRequestMetadata {
    private final UUID sessionId;
    private final TransportProtos.SessionType type;
    private final String nodeId;
}
