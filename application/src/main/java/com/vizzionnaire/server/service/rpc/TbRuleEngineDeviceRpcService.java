package com.vizzionnaire.server.service.rpc;

import com.vizzionnaire.rule.engine.api.RuleEngineRpcService;
import com.vizzionnaire.server.common.msg.rpc.FromDeviceRpcResponse;

/**
 * Created by ashvayka on 16.04.18.
 */
public interface TbRuleEngineDeviceRpcService extends RuleEngineRpcService {

    /**
     * Handles the RPC response from the Device Actor (Transport).
     *
     * @param response the RPC response
     */
    void processRpcResponseFromDevice(FromDeviceRpcResponse response);
}
