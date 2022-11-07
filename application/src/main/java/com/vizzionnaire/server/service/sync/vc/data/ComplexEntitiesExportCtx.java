package com.vizzionnaire.server.service.sync.vc.data;

import java.util.HashMap;
import java.util.Map;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportSettings;
import com.vizzionnaire.server.common.data.sync.vc.request.create.ComplexVersionCreateRequest;

public class ComplexEntitiesExportCtx extends EntitiesExportCtx<ComplexVersionCreateRequest> {

    private final Map<EntityType, EntityExportSettings> settings = new HashMap<>();

    public ComplexEntitiesExportCtx(User user, CommitGitRequest commit, ComplexVersionCreateRequest request) {
        super(user, commit, request);
        request.getEntityTypes().forEach((type, config) -> settings.put(type, buildExportSettings(config)));
    }

    public EntityExportSettings getSettings(EntityType entityType) {
        return settings.get(entityType);
    }

    @Override
    public EntityExportSettings getSettings() {
        throw new RuntimeException("Not implemented. Use EntityTypeExportCtx instead!");
    }
}
