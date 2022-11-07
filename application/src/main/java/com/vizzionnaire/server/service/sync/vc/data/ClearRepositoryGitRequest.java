package com.vizzionnaire.server.service.sync.vc.data;

import com.vizzionnaire.server.common.data.id.TenantId;

public class ClearRepositoryGitRequest extends VoidGitRequest {

    public ClearRepositoryGitRequest(TenantId tenantId) {
        super(tenantId);
    }

    public boolean requiresSettings() {
        return false;
    }

}
