package com.vizzionnaire.server.dao.device;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("DeviceCredentialsCache")
public class DeviceCredentialsCaffeineCache extends CaffeineTbTransactionalCache<String, DeviceCredentials> {

    public DeviceCredentialsCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.DEVICE_CREDENTIALS_CACHE);
    }

}
