package com.vizzionnaire.server.dao.tenant;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.TenantInfo;
import com.vizzionnaire.server.common.data.id.TenantId;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("TenantExistsCache")
public class TenantExistsCaffeineCache extends CaffeineTbTransactionalCache<TenantId, Boolean> {

    public TenantExistsCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.TENANTS_EXIST_CACHE);
    }

}
