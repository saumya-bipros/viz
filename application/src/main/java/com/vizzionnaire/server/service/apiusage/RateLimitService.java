package com.vizzionnaire.server.service.apiusage;

import com.vizzionnaire.server.common.data.id.TenantId;

public interface RateLimitService {

    boolean checkEntityExportLimit(TenantId tenantId);

    boolean checkEntityImportLimit(TenantId tenantId);

}
