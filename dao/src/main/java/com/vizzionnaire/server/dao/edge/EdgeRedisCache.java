package com.vizzionnaire.server.dao.edge;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CacheSpecsMap;
import com.vizzionnaire.server.cache.RedisTbTransactionalCache;
import com.vizzionnaire.server.cache.TBRedisCacheConfiguration;
import com.vizzionnaire.server.cache.TbFSTRedisSerializer;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.edge.Edge;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("EdgeCache")
public class EdgeRedisCache extends RedisTbTransactionalCache<EdgeCacheKey, Edge> {

    public EdgeRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.EDGE_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
