package com.vizzionnaire.rule.engine.api.msg;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.ToDeviceActorNotificationMsg;

@Data
@AllArgsConstructor
public class DeviceEdgeUpdateMsg implements ToDeviceActorNotificationMsg {

    private final TenantId tenantId;
    private final DeviceId deviceId;
    private final EdgeId edgeId;

    @Override
    public MsgType getMsgType() {
        return MsgType.DEVICE_EDGE_UPDATE_TO_DEVICE_ACTOR_MSG;
    }
}
