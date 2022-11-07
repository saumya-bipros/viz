package com.vizzionnaire.server.actors.device;

import com.vizzionnaire.server.service.rpc.ToDeviceRpcRequestActorMsg;

import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
public class ToDeviceRpcRequestMetadata {
    private final ToDeviceRpcRequestActorMsg msg;
    private final boolean sent;
    private int retries;
    private boolean delivered;
}
