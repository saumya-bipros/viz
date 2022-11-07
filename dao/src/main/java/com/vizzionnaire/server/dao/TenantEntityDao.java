package com.vizzionnaire.server.dao;

import com.vizzionnaire.server.common.data.id.TenantId;

public interface TenantEntityDao {

    Long countByTenantId(TenantId tenantId);
}
