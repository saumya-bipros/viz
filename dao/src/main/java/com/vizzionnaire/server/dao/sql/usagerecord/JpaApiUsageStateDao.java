package com.vizzionnaire.server.dao.sql.usagerecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.ApiUsageStateEntity;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;
import com.vizzionnaire.server.dao.usagerecord.ApiUsageStateDao;

import java.util.UUID;

/**
 * @author Andrii Shvaika
 */
@Component
public class JpaApiUsageStateDao extends JpaAbstractDao<ApiUsageStateEntity, ApiUsageState> implements ApiUsageStateDao {

    private final ApiUsageStateRepository apiUsageStateRepository;

    public JpaApiUsageStateDao(ApiUsageStateRepository apiUsageStateRepository) {
        this.apiUsageStateRepository = apiUsageStateRepository;
    }

    @Override
    protected Class<ApiUsageStateEntity> getEntityClass() {
        return ApiUsageStateEntity.class;
    }

    @Override
    protected JpaRepository<ApiUsageStateEntity, UUID> getRepository() {
        return apiUsageStateRepository;
    }

    @Override
    public ApiUsageState findTenantApiUsageState(UUID tenantId) {
        return DaoUtil.getData(apiUsageStateRepository.findByTenantId(tenantId));
    }

    @Override
    public ApiUsageState findApiUsageStateByEntityId(EntityId entityId) {
        return DaoUtil.getData(apiUsageStateRepository.findByEntityIdAndEntityType(entityId.getId(), entityId.getEntityType().name()));
    }

    @Override
    public void deleteApiUsageStateByTenantId(TenantId tenantId) {
        apiUsageStateRepository.deleteApiUsageStateByTenantId(tenantId.getId());
    }

    @Override
    public void deleteApiUsageStateByEntityId(EntityId entityId) {
        apiUsageStateRepository.deleteByEntityIdAndEntityType(entityId.getId(), entityId.getEntityType().name());
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.API_USAGE_STATE;
    }

}
