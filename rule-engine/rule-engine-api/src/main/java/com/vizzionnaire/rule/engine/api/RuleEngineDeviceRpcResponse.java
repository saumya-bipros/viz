package com.vizzionnaire.rule.engine.api;

import lombok.Builder;
import lombok.Data;

import java.util.Optional;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.rpc.RpcError;

/**
 * Created by ashvayka on 02.04.18.
 */
@Data
@Builder
public final class RuleEngineDeviceRpcResponse {

    private final DeviceId deviceId;
    private final int requestId;
    private final Optional<String> response;
    private final Optional<RpcError> error;

}
