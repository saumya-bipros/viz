package com.vizzionnaire.server.dao.entity;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.query.EntityCountQuery;
import com.vizzionnaire.server.common.data.query.EntityData;
import com.vizzionnaire.server.common.data.query.EntityDataQuery;

public interface EntityService {

    ListenableFuture<String> fetchEntityNameAsync(TenantId tenantId, EntityId entityId);

    CustomerId fetchEntityCustomerId(TenantId tenantId, EntityId entityId);

    long countEntitiesByQuery(TenantId tenantId, CustomerId customerId, EntityCountQuery query);

    PageData<EntityData> findEntityDataByQuery(TenantId tenantId, CustomerId customerId, EntityDataQuery query);

}
