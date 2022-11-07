package com.vizzionnaire.server.dao.ota;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.OtaPackageInfo;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("OtaPackageCache")
public class OtaPackageCaffeineCache extends CaffeineTbTransactionalCache<OtaPackageCacheKey, OtaPackageInfo> {

    public OtaPackageCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.OTA_PACKAGE_CACHE);
    }

}
