package com.vizzionnaire.server.service.sync.ie.exporting.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesExportCtx;

import java.util.Set;

public abstract class BaseEntityExportService<I extends EntityId, E extends ExportableEntity<I>, D extends EntityExportData<E>> extends DefaultEntityExportService<I, E, D> {

    @Override
    protected void setAdditionalExportData(EntitiesExportCtx<?> ctx, E entity, D exportData) throws ThingsboardException {
        setRelatedEntities(ctx, entity, (D) exportData);
        super.setAdditionalExportData(ctx, entity, exportData);
    }

    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, E mainEntity, D exportData) {
    }

    protected D newExportData() {
        return (D) new EntityExportData<E>();
    }

    public abstract Set<EntityType> getSupportedEntityTypes();

    protected void replaceUuidsRecursively(EntitiesExportCtx<?> ctx, JsonNode node, Set<String> skipFieldsSet) {
        JacksonUtil.replaceUuidsRecursively(node, skipFieldsSet, uuid -> getExternalIdOrElseInternalByUuid(ctx, uuid));
    }

}
