package com.vizzionnaire.server.dao.edge;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.edge.Edge;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("EdgeCache")
public class EdgeCaffeineCache extends CaffeineTbTransactionalCache<EdgeCacheKey, Edge> {

    public EdgeCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.EDGE_CACHE);
    }

}
