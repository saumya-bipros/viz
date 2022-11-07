package com.vizzionnaire.server.service.sync.ie.exporting.impl;

import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.EntityView;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesExportCtx;

import java.util.Set;

@Service
@TbCoreComponent
public class EntityViewExportService extends BaseEntityExportService<EntityViewId, EntityView, EntityExportData<EntityView>> {

    @Override
    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, EntityView entityView, EntityExportData<EntityView> exportData) {
        entityView.setEntityId(getExternalIdOrElseInternal(ctx, entityView.getEntityId()));
        entityView.setCustomerId(getExternalIdOrElseInternal(ctx, entityView.getCustomerId()));
    }

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return Set.of(EntityType.ENTITY_VIEW);
    }

}
