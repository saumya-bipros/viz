package com.vizzionnaire.server.service.sync.vc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.cache.CaffeineTbTransactionalCache;
import com.vizzionnaire.server.common.data.CacheConstants;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.gen.transport.TransportProtos;

import java.util.UUID;

@ConditionalOnProperty(prefix = "cache", value = "type", havingValue = "caffeine", matchIfMissing = true)
@Service("VersionControlTaskCache")
public class VersionControlTaskCaffeineCache extends CaffeineTbTransactionalCache<UUID, VersionControlTaskCacheEntry> {

    public VersionControlTaskCaffeineCache(CacheManager cacheManager) {
        super(cacheManager, CacheConstants.VERSION_CONTROL_TASK_CACHE);
    }

}
