package com.vizzionnaire.server.dao.device;

import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;

@Data
public class DeviceProfileEvictEvent {

    private final TenantId tenantId;
    private final String newName;
    private final String oldName;
    private final DeviceProfileId deviceProfileId;
    private final boolean defaultProfile;

}
