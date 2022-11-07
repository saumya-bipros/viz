package com.vizzionnaire.server.service.sync.vc.data;

import lombok.Getter;

import java.util.List;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.EntityVersionsDiff;

@Getter
public class VersionsDiffGitRequest extends PendingGitRequest<List<EntityVersionsDiff>> {

    private final String path;
    private final String versionId1;
    private final String versionId2;

    public VersionsDiffGitRequest(TenantId tenantId, String path, String versionId1, String versionId2) {
        super(tenantId);
        this.path = path;
        this.versionId1 = versionId1;
        this.versionId2 = versionId2;
    }

}
