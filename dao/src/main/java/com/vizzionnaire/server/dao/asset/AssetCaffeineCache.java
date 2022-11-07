package com.vizzionnaire.server.dao.asset;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.asset.Asset;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("AssetCache")
public class AssetCaffeineCache extends CaffeineTbTransactionalCache<AssetCacheKey, Asset> {

    public AssetCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.ASSET_CACHE);
    }

}
