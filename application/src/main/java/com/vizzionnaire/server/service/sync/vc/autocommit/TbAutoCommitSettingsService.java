package com.vizzionnaire.server.service.sync.vc.autocommit;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.AutoCommitSettings;
import com.vizzionnaire.server.common.data.sync.vc.RepositorySettings;

public interface TbAutoCommitSettingsService {

    AutoCommitSettings get(TenantId tenantId);

    AutoCommitSettings save(TenantId tenantId, AutoCommitSettings settings);

    boolean delete(TenantId tenantId);

}
