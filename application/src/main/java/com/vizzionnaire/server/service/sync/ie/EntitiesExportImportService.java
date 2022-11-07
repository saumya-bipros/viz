package com.vizzionnaire.server.service.sync.ie;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.common.data.sync.ie.EntityImportResult;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesExportCtx;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesImportCtx;

import java.util.Comparator;

public interface EntitiesExportImportService {

    <E extends ExportableEntity<I>, I extends EntityId> EntityExportData<E> exportEntity(EntitiesExportCtx<?> ctx, I entityId) throws VizzionnaireException;

    <E extends ExportableEntity<I>, I extends EntityId> EntityImportResult<E> importEntity(EntitiesImportCtx ctx, EntityExportData<E> exportData) throws VizzionnaireException;


    void saveReferencesAndRelations(EntitiesImportCtx ctx) throws VizzionnaireException;

    Comparator<EntityType> getEntityTypeComparatorForImport();

}
