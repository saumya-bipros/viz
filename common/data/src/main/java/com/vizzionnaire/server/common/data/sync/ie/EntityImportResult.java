package com.vizzionnaire.server.common.data.sync.ie;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.sync.ThrowingRunnable;

import lombok.Data;

@Data
public class EntityImportResult<E extends ExportableEntity<? extends EntityId>> {

    private E savedEntity;
    private E oldEntity;
    private EntityType entityType;

    private ThrowingRunnable saveReferencesCallback = () -> {};
    private ThrowingRunnable sendEventsCallback = () -> {};

    private boolean updatedAllExternalIds = true;

    private boolean created;
    private boolean updated;
    private boolean updatedRelatedEntities;

    public void addSaveReferencesCallback(ThrowingRunnable callback) {
        this.saveReferencesCallback = this.saveReferencesCallback.andThen(callback);
    }

    public void addSendEventsCallback(ThrowingRunnable callback) {
        this.sendEventsCallback = this.sendEventsCallback.andThen(callback);
    }

}
