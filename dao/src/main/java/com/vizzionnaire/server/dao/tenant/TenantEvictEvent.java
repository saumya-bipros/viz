package com.vizzionnaire.server.dao.tenant;

import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;

@Data
public class TenantEvictEvent {
    private final TenantId tenantId;
    private final boolean invalidateExists;
}
