package com.vizzionnaire.server.service.sync.vc.data;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;

import lombok.Getter;

@Getter
public class EntityContentGitRequest extends PendingGitRequest<EntityExportData> {

    private final String versionId;
    private final EntityId entityId;

    public EntityContentGitRequest(TenantId tenantId, String versionId, EntityId entityId) {
        super(tenantId);
        this.versionId = versionId;
        this.entityId = entityId;
    }
}
