package com.vizzionnaire.server.service.sync.vc.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.RepositorySettings;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("RepositorySettingsCache")
public class RepositorySettingsCaffeineCache extends CaffeineTbTransactionalCache<TenantId, RepositorySettings> {

    public RepositorySettingsCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.REPOSITORY_SETTINGS_CACHE);
    }

}
