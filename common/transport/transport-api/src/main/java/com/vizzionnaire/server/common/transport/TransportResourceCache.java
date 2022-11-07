package com.vizzionnaire.server.common.transport;

import java.util.Optional;

import com.vizzionnaire.server.common.data.ResourceType;
import com.vizzionnaire.server.common.data.TbResource;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface TransportResourceCache {

    Optional<TbResource> get(TenantId tenantId, ResourceType resourceType, String resourceId);

    void update(TenantId tenantId, ResourceType resourceType, String resourceI);

    void evict(TenantId tenantId, ResourceType resourceType, String resourceId);
}
