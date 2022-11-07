package com.vizzionnaire.server.service.sync.vc.data;

import lombok.Getter;

import java.util.List;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;

@Getter
public class EntitiesContentGitRequest extends PendingGitRequest<List<EntityExportData>> {

    private final String versionId;
    private final EntityType entityType;

    public EntitiesContentGitRequest(TenantId tenantId, String versionId, EntityType entityType) {
        super(tenantId);
        this.versionId = versionId;
        this.entityType = entityType;
    }
}
