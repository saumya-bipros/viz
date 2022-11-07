package com.vizzionnaire.server.service.sync.vc.autocommit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.AutoCommitSettings;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("AutoCommitSettingsCache")
public class AutoCommitSettingsCaffeineCache extends CaffeineTbTransactionalCache<TenantId, AutoCommitSettings> {

    public AutoCommitSettingsCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.AUTO_COMMIT_SETTINGS_CACHE);
    }

}
