package com.vizzionnaire.server.dao.tenant;

import java.util.function.Consumer;

import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.TenantProfileId;

public interface TbTenantProfileCache {

    TenantProfile get(TenantId tenantId);

    TenantProfile get(TenantProfileId tenantProfileId);

    void put(TenantProfile profile);

    void evict(TenantProfileId id);

    void evict(TenantId id);

    void addListener(TenantId tenantId, EntityId listenerId, Consumer<TenantProfile> profileListener);

    void removeListener(TenantId tenantId, EntityId listenerId);

}
