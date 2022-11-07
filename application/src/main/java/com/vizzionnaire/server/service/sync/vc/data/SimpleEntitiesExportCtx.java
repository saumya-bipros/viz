package com.vizzionnaire.server.service.sync.vc.data;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportSettings;
import com.vizzionnaire.server.common.data.sync.vc.request.create.SingleEntityVersionCreateRequest;

import lombok.Getter;

public class SimpleEntitiesExportCtx extends EntitiesExportCtx<SingleEntityVersionCreateRequest> {

    @Getter
    private final EntityExportSettings settings;

    public SimpleEntitiesExportCtx(User user, CommitGitRequest commit, SingleEntityVersionCreateRequest request) {
        this(user, commit, request, request != null ? buildExportSettings(request.getConfig()) : null);
    }

    public SimpleEntitiesExportCtx(User user, CommitGitRequest commit, SingleEntityVersionCreateRequest request, EntityExportSettings settings) {
        super(user, commit, request);
        this.settings = settings;
    }
}
