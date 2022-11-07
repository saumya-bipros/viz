package com.vizzionnaire.server.dao.sql.query;

import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.query.EntityCountQuery;
import com.vizzionnaire.server.common.data.query.EntityData;
import com.vizzionnaire.server.common.data.query.EntityDataQuery;

public interface EntityQueryRepository {

    long countEntitiesByQuery(TenantId tenantId, CustomerId customerId, EntityCountQuery query);

    PageData<EntityData> findEntityDataByQuery(TenantId tenantId, CustomerId customerId, EntityDataQuery query);

    PageData<EntityData> findEntityDataByQueryInternal(EntityDataQuery query);

}
