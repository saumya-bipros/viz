package com.vizzionnaire.rule.engine.api.msg;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.kv.AttributeKey;
import com.vizzionnaire.server.common.data.kv.AttributeKvEntry;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.ToDeviceActorNotificationMsg;

import java.util.List;
import java.util.Set;

/**
 * @author Andrew Shvayka
 */
@ToString
@AllArgsConstructor
public class DeviceAttributesEventNotificationMsg implements ToDeviceActorNotificationMsg {

    @Getter
    private final TenantId tenantId;
    @Getter
    private final DeviceId deviceId;
    @Getter
    private final Set<AttributeKey> deletedKeys;
    @Getter
    private final String scope;
    @Getter
    private final List<AttributeKvEntry> values;
    @Getter
    private final boolean deleted;

    public static DeviceAttributesEventNotificationMsg onUpdate(TenantId tenantId, DeviceId deviceId, String scope, List<AttributeKvEntry> values) {
        return new DeviceAttributesEventNotificationMsg(tenantId, deviceId, null, scope, values, false);
    }

    public static DeviceAttributesEventNotificationMsg onDelete(TenantId tenantId, DeviceId deviceId, Set<AttributeKey> keys) {
        return new DeviceAttributesEventNotificationMsg(tenantId, deviceId, keys, null, null, true);
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.DEVICE_ATTRIBUTES_UPDATE_TO_DEVICE_ACTOR_MSG;
    }
}
