package com.vizzionnaire.server.dao.sql.widget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.widget.WidgetType;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.common.data.widget.WidgetTypeInfo;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.WidgetTypeDetailsEntity;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;
import com.vizzionnaire.server.dao.widget.WidgetTypeDao;

import java.util.List;
import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 4/29/2017.
 */
@Component
public class JpaWidgetTypeDao extends JpaAbstractDao<WidgetTypeDetailsEntity, WidgetTypeDetails> implements WidgetTypeDao {

    @Autowired
    private WidgetTypeRepository widgetTypeRepository;

    @Override
    protected Class<WidgetTypeDetailsEntity> getEntityClass() {
        return WidgetTypeDetailsEntity.class;
    }

    @Override
    protected JpaRepository<WidgetTypeDetailsEntity, UUID> getRepository() {
        return widgetTypeRepository;
    }

    @Override
    public WidgetType findWidgetTypeById(TenantId tenantId, UUID widgetTypeId) {
        return DaoUtil.getData(widgetTypeRepository.findWidgetTypeById(widgetTypeId));
    }

    @Override
    public List<WidgetType> findWidgetTypesByTenantIdAndBundleAlias(UUID tenantId, String bundleAlias) {
        return DaoUtil.convertDataList(widgetTypeRepository.findWidgetTypesByTenantIdAndBundleAlias(tenantId, bundleAlias));
    }

    @Override
    public List<WidgetTypeDetails> findWidgetTypesDetailsByTenantIdAndBundleAlias(UUID tenantId, String bundleAlias) {
        return DaoUtil.convertDataList(widgetTypeRepository.findByTenantIdAndBundleAlias(tenantId, bundleAlias));
    }

    @Override
    public List<WidgetTypeInfo> findWidgetTypesInfosByTenantIdAndBundleAlias(UUID tenantId, String bundleAlias) {
        return DaoUtil.convertDataList(widgetTypeRepository.findWidgetTypesInfosByTenantIdAndBundleAlias(tenantId, bundleAlias));
    }

    @Override
    public WidgetType findByTenantIdBundleAliasAndAlias(UUID tenantId, String bundleAlias, String alias) {
        return DaoUtil.getData(widgetTypeRepository.findWidgetTypeByTenantIdAndBundleAliasAndAlias(tenantId, bundleAlias, alias));
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGET_TYPE;
    }

}
