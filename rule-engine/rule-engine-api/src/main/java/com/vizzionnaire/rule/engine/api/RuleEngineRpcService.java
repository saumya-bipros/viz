package com.vizzionnaire.rule.engine.api;

import java.util.UUID;
import java.util.function.Consumer;

import com.vizzionnaire.server.common.data.id.DeviceId;

/**
 * Created by ashvayka on 02.04.18.
 */
public interface RuleEngineRpcService {

    void sendRpcReplyToDevice(String serviceId, UUID sessionId, int requestId, String body);

    void sendRpcRequestToDevice(RuleEngineDeviceRpcRequest request, Consumer<RuleEngineDeviceRpcResponse> consumer);

}
