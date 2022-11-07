package com.vizzionnaire.server.dao.usagerecord;

import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.Dao;

import java.util.UUID;

public interface ApiUsageStateDao extends Dao<ApiUsageState> {

    /**
     * Save or update usage record object
     *
     * @param apiUsageState the usage record
     * @return saved usage record entity
     */
    ApiUsageState save(TenantId tenantId, ApiUsageState apiUsageState);

    /**
     * Find usage record by tenantId.
     *
     * @param tenantId the tenantId
     * @return the corresponding usage record
     */
    ApiUsageState findTenantApiUsageState(UUID tenantId);

    ApiUsageState findApiUsageStateByEntityId(EntityId entityId);

    /**
     * Delete usage record by tenantId.
     *
     * @param tenantId the tenantId
     */
    void deleteApiUsageStateByTenantId(TenantId tenantId);

    void deleteApiUsageStateByEntityId(EntityId entityId);
}
