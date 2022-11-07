package com.vizzionnaire.server.service.sync.vc.data;

import java.util.List;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.VersionedEntityInfo;

public class ListEntitiesGitRequest extends PendingGitRequest<List<VersionedEntityInfo>> {

    public ListEntitiesGitRequest(TenantId tenantId) {
        super(tenantId);
    }

}
