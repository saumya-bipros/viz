package com.vizzionnaire.server.service.sync.vc.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CacheSpecsMap;
import com.vizzionnaire.server.cache.RedisTbTransactionalCache;
import com.vizzionnaire.server.cache.TBRedisCacheConfiguration;
import com.vizzionnaire.server.cache.TbFSTRedisSerializer;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.vc.RepositorySettings;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("RepositorySettingsCache")
public class RepositorySettingsRedisCache extends RedisTbTransactionalCache<TenantId, RepositorySettings> {

    public RepositorySettingsRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.REPOSITORY_SETTINGS_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
