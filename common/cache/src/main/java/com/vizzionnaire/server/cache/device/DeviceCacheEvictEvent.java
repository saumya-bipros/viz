package com.vizzionnaire.server.cache.device;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;

@Data
public class DeviceCacheEvictEvent {

    private final TenantId tenantId;
    private final DeviceId deviceId;
    private final String newName;
    private final String oldName;

}
