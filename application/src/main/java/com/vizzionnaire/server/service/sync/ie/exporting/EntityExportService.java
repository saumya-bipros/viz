package com.vizzionnaire.server.service.sync.ie.exporting;

import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesExportCtx;

public interface EntityExportService<I extends EntityId, E extends ExportableEntity<I>, D extends EntityExportData<E>> {

    D getExportData(EntitiesExportCtx<?> ctx, I entityId) throws VizzionnaireException;

}
