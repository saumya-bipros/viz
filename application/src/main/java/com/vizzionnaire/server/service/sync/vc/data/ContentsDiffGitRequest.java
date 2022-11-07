package com.vizzionnaire.server.service.sync.vc.data;

import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Getter;

@Getter
public class ContentsDiffGitRequest extends PendingGitRequest<String> {

    private final String content1;
    private final String content2;

    public ContentsDiffGitRequest(TenantId tenantId, String content1, String content2) {
        super(tenantId);
        this.content1 = content1;
        this.content2 = content2;
    }

}
