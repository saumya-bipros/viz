package com.vizzionnaire.server.service.rpc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.ToDeviceActorNotificationMsg;
import com.vizzionnaire.server.common.msg.rpc.FromDeviceRpcResponse;

@ToString
@RequiredArgsConstructor
public class FromDeviceRpcResponseActorMsg implements ToDeviceActorNotificationMsg {

    @Getter
    private final Integer requestId;
    @Getter
    private final TenantId tenantId;
    @Getter
    private final DeviceId deviceId;

    @Getter
    private final FromDeviceRpcResponse msg;

    @Override
    public MsgType getMsgType() {
        return MsgType.DEVICE_RPC_RESPONSE_TO_DEVICE_ACTOR_MSG;
    }
}
