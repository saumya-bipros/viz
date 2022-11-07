package com.vizzionnaire.server.dao.sql.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.query.EntityCountQuery;
import com.vizzionnaire.server.common.data.query.EntityData;
import com.vizzionnaire.server.common.data.query.EntityDataQuery;
import com.vizzionnaire.server.dao.entity.EntityQueryDao;

@Component
public class JpaEntityQueryDao implements EntityQueryDao {

    @Autowired
    private EntityQueryRepository entityQueryRepository;

    @Override
    public long countEntitiesByQuery(TenantId tenantId, CustomerId customerId, EntityCountQuery query) {
        return entityQueryRepository.countEntitiesByQuery(tenantId, customerId, query);
    }

    @Override
    public PageData<EntityData> findEntityDataByQuery(TenantId tenantId, CustomerId customerId, EntityDataQuery query) {
        return entityQueryRepository.findEntityDataByQuery(tenantId, customerId, query);
    }
}
