package com.vizzionnaire.server.cache.device;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CacheSpecsMap;
import com.vizzionnaire.server.cache.RedisTbTransactionalCache;
import com.vizzionnaire.server.cache.TBRedisCacheConfiguration;
import com.vizzionnaire.server.cache.TbFSTRedisSerializer;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.Device;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("DeviceCache")
public class DeviceRedisCache extends RedisTbTransactionalCache<DeviceCacheKey, Device> {

    public DeviceRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.DEVICE_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
