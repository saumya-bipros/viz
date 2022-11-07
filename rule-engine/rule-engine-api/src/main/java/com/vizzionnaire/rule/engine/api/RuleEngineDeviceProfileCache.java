package com.vizzionnaire.rule.engine.api;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;

/**
 * Created by ashvayka on 02.04.18.
 */
public interface RuleEngineDeviceProfileCache {

    DeviceProfile get(TenantId tenantId, DeviceProfileId deviceProfileId);

    DeviceProfile get(TenantId tenantId, DeviceId deviceId);

    void addListener(TenantId tenantId, EntityId listenerId, Consumer<DeviceProfile> profileListener, BiConsumer<DeviceId, DeviceProfile> devicelistener);

    void removeListener(TenantId tenantId, EntityId listenerId);

}
