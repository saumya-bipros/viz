package com.vizzionnaire.server.dao.device;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CacheSpecsMap;
import com.vizzionnaire.server.cache.RedisTbTransactionalCache;
import com.vizzionnaire.server.cache.TBRedisCacheConfiguration;
import com.vizzionnaire.server.cache.TbFSTRedisSerializer;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.DeviceProfile;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "redis")
@Service("DeviceProfileCache")
public class DeviceProfileRedisCache extends RedisTbTransactionalCache<DeviceProfileCacheKey, DeviceProfile> {

    public DeviceProfileRedisCache(TBRedisCacheConfiguration configuration, CacheSpecsMap cacheSpecsMap, RedisConnectionFactory connectionFactory) {
        super(CacheConstants.DEVICE_PROFILE_CACHE, cacheSpecsMap, connectionFactory, configuration, new TbFSTRedisSerializer<>());
    }
}
