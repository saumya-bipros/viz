package com.vizzionnaire.server.dao.device;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.DeviceProfile;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("DeviceProfileCache")
public class DeviceProfileCaffeineCache extends CaffeineTbTransactionalCache<DeviceProfileCacheKey, DeviceProfile> {

    public DeviceProfileCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.DEVICE_PROFILE_CACHE);
    }

}
