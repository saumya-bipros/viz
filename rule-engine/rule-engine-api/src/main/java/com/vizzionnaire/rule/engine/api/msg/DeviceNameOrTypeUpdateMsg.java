package com.vizzionnaire.rule.engine.api.msg;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.ToDeviceActorNotificationMsg;

@Data
@AllArgsConstructor
public class DeviceNameOrTypeUpdateMsg implements ToDeviceActorNotificationMsg {

    private static final long serialVersionUID = -5738949227650536685L;

    private final TenantId tenantId;
    private final DeviceId deviceId;
    private final String deviceName;
    private final String deviceType;

    @Override
    public MsgType getMsgType() {
        return MsgType.DEVICE_NAME_OR_TYPE_UPDATE_TO_DEVICE_ACTOR_MSG;
    }
}
