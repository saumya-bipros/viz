package com.vizzionnaire.server.service.sync.vc.autocommit;

import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.TbTransactionalCache;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.AutoCommitSettings;
import com.vizzionnaire.server.dao.settings.AdminSettingsService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.TbAbstractVersionControlSettingsService;

@Service
@TbCoreComponent
public class DefaultTbAutoCommitSettingsService extends TbAbstractVersionControlSettingsService<AutoCommitSettings> implements TbAutoCommitSettingsService {

    public static final String SETTINGS_KEY = "autoCommitSettings";

    public DefaultTbAutoCommitSettingsService(AdminSettingsService adminSettingsService, TbTransactionalCache<TenantId, AutoCommitSettings> cache) {
        super(adminSettingsService, cache, AutoCommitSettings.class, SETTINGS_KEY);
    }

}
