package com.vizzionnaire.server.dao;

import com.vizzionnaire.server.common.data.id.TenantId;

public interface TenantEntityWithDataDao {

    Long sumDataSizeByTenantId(TenantId tenantId);
}
