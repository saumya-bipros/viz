package com.vizzionnaire.server.service.profile;

import com.vizzionnaire.rule.engine.api.RuleEngineDeviceProfileCache;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface TbDeviceProfileCache extends RuleEngineDeviceProfileCache {

    void evict(TenantId tenantId, DeviceProfileId id);

    void evict(TenantId tenantId, DeviceId id);

    DeviceProfile find(DeviceProfileId deviceProfileId);

    DeviceProfile findOrCreateDeviceProfile(TenantId tenantId, String deviceType);
}
