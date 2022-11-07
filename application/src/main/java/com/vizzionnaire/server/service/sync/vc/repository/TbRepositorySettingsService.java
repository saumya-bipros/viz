package com.vizzionnaire.server.service.sync.vc.repository;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.RepositorySettings;

public interface TbRepositorySettingsService {

    RepositorySettings restore(TenantId tenantId, RepositorySettings versionControlSettings);

    RepositorySettings get(TenantId tenantId);

    RepositorySettings save(TenantId tenantId, RepositorySettings versionControlSettings);

    boolean delete(TenantId tenantId);

}
