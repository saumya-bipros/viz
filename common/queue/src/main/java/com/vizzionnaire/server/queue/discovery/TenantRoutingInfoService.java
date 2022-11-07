package com.vizzionnaire.server.queue.discovery;

import com.vizzionnaire.server.common.data.id.TenantId;

public interface TenantRoutingInfoService {

    TenantRoutingInfo getRoutingInfo(TenantId tenantId);
}
