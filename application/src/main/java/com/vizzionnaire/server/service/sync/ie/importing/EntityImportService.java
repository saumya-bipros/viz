package com.vizzionnaire.server.service.sync.ie.importing;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.common.data.sync.ie.EntityImportResult;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesImportCtx;

public interface EntityImportService<I extends EntityId, E extends ExportableEntity<I>, D extends EntityExportData<E>> {

    EntityImportResult<E> importEntity(EntitiesImportCtx ctx, D exportData) throws ThingsboardException;

    EntityType getEntityType();

}
