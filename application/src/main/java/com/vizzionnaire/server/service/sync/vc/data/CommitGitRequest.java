package com.vizzionnaire.server.service.sync.vc.data;

import lombok.Getter;

import java.util.UUID;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.VersionCreationResult;
import com.vizzionnaire.server.common.data.sync.vc.request.create.VersionCreateRequest;

public class CommitGitRequest extends PendingGitRequest<VersionCreationResult> {

    @Getter
    private final UUID txId;
    private final VersionCreateRequest request;

    public CommitGitRequest(TenantId tenantId, VersionCreateRequest request) {
        super(tenantId);
        this.txId = UUID.randomUUID();
        this.request = request;
    }

}
