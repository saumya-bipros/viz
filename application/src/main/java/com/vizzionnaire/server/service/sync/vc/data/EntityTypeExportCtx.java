package com.vizzionnaire.server.service.sync.vc.data;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportSettings;
import com.vizzionnaire.server.common.data.sync.vc.request.create.EntityTypeVersionCreateConfig;
import com.vizzionnaire.server.common.data.sync.vc.request.create.SyncStrategy;
import com.vizzionnaire.server.common.data.sync.vc.request.create.VersionCreateRequest;

public class EntityTypeExportCtx extends EntitiesExportCtx<VersionCreateRequest> {

    @Getter
    private final EntityType entityType;
    @Getter
    private final boolean overwrite;
    @Getter
    private final EntityExportSettings settings;

    public EntityTypeExportCtx(EntitiesExportCtx<?> parent, EntityTypeVersionCreateConfig config, SyncStrategy defaultSyncStrategy, EntityType entityType) {
        super(parent);
        this.entityType = entityType;
        this.settings = EntityExportSettings.builder()
                .exportRelations(config.isSaveRelations())
                .exportAttributes(config.isSaveAttributes())
                .exportCredentials(config.isSaveCredentials())
                .build();
        this.overwrite = ObjectUtils.defaultIfNull(config.getSyncStrategy(), defaultSyncStrategy) == SyncStrategy.OVERWRITE;
    }

}
