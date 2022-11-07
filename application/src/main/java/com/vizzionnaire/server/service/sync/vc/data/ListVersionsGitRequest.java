package com.vizzionnaire.server.service.sync.vc.data;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.sync.vc.EntityVersion;

public class ListVersionsGitRequest extends PendingGitRequest<PageData<EntityVersion>> {

    public ListVersionsGitRequest(TenantId tenantId) {
        super(tenantId);
    }

}
