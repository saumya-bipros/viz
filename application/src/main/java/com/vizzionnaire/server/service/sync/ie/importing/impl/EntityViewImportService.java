package com.vizzionnaire.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.EntityView;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.entityview.TbEntityViewService;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class EntityViewImportService extends BaseEntityImportService<EntityViewId, EntityView, EntityExportData<EntityView>> {

    private final EntityViewService entityViewService;

    @Lazy
    @Autowired
    private TbEntityViewService tbEntityViewService;

    @Override
    protected void setOwner(TenantId tenantId, EntityView entityView, IdProvider idProvider) {
        entityView.setTenantId(tenantId);
        entityView.setCustomerId(idProvider.getInternalId(entityView.getCustomerId()));
    }

    @Override
    protected EntityView prepare(EntitiesImportCtx ctx, EntityView entityView, EntityView old, EntityExportData<EntityView> exportData, IdProvider idProvider) {
        entityView.setEntityId(idProvider.getInternalId(entityView.getEntityId()));
        return entityView;
    }

    @Override
    protected EntityView saveOrUpdate(EntitiesImportCtx ctx, EntityView entityView, EntityExportData<EntityView> exportData, IdProvider idProvider) {
        return entityViewService.saveEntityView(entityView);
    }

    @Override
    protected void onEntitySaved(User user, EntityView savedEntityView, EntityView oldEntityView) throws ThingsboardException {
        tbEntityViewService.updateEntityViewAttributes(user.getTenantId(), savedEntityView, oldEntityView, user);
        super.onEntitySaved(user, savedEntityView, oldEntityView);
        clusterService.broadcastEntityStateChangeEvent(savedEntityView.getTenantId(), savedEntityView.getId(),
                oldEntityView == null ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);
    }

    @Override
    protected EntityView deepCopy(EntityView entityView) {
        return new EntityView(entityView);
    }

    @Override
    protected void cleanupForComparison(EntityView e) {
        super.cleanupForComparison(e);
        if (e.getCustomerId() != null && e.getCustomerId().isNullUid()) {
            e.setCustomerId(null);
        }
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.ENTITY_VIEW;
    }

}
