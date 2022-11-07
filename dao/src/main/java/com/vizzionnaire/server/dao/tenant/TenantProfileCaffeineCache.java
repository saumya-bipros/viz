package com.vizzionnaire.server.dao.tenant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.TenantProfile;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("TenantProfileCache")
public class TenantProfileCaffeineCache extends CaffeineTbTransactionalCache<TenantProfileCacheKey, TenantProfile> {

    public TenantProfileCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.TENANT_PROFILE_CACHE);
    }

}
