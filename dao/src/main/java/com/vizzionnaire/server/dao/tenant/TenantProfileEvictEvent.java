package com.vizzionnaire.server.dao.tenant;

import com.vizzionnaire.server.common.data.id.TenantProfileId;

import lombok.Data;

@Data
public class TenantProfileEvictEvent {
    private final TenantProfileId tenantProfileId;
    private final boolean defaultProfile;
}
