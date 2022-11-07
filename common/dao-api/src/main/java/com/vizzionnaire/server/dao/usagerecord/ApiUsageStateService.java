package com.vizzionnaire.server.dao.usagerecord;

import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.id.ApiUsageStateId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface ApiUsageStateService {

    ApiUsageState createDefaultApiUsageState(TenantId id, EntityId entityId);

    ApiUsageState update(ApiUsageState apiUsageState);

    ApiUsageState findTenantApiUsageState(TenantId tenantId);

    ApiUsageState findApiUsageStateByEntityId(EntityId entityId);

    void deleteApiUsageStateByTenantId(TenantId tenantId);

    void deleteApiUsageStateByEntityId(EntityId entityId);

    ApiUsageState findApiUsageStateById(TenantId tenantId, ApiUsageStateId id);
}
