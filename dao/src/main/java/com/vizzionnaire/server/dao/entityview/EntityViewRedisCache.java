package com.vizzionnaire.server.dao.entityview;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CacheSpecsMap;
import com.vizzionnaire.server.cache.RedisTbTransactionalCache;
import com.vizzionnaire.server.cache.TBRedisCacheConfiguration;
import com.vizzionnaire.server.cache.TbFSTRedisSerializer;
import com.vizzionnaire.server.common.data.CacheConstants;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("EntityViewCache")
public class EntityViewRedisCache extends RedisTbTransactionalCache<EntityViewCacheKey, EntityViewCacheValue> {

    public EntityViewRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.ENTITY_VIEW_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
