package com.vizzionnaire.server.service.sync.vc.data;

import java.util.List;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.BranchInfo;

public class ListBranchesGitRequest extends PendingGitRequest<List<BranchInfo>> {

    public ListBranchesGitRequest(TenantId tenantId) {
        super(tenantId);
    }

}
