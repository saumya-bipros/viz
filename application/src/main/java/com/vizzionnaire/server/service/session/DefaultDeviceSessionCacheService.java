package com.vizzionnaire.server.service.session;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vizzionnaire.server.cache.TbTransactionalCache;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.gen.transport.TransportProtos.DeviceSessionsCacheEntry;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.vizzionnaire.server.common.data.CacheConstants.SESSIONS_CACHE;

import java.io.Serializable;
import java.util.Collections;

/**
 * Created by ashvayka on 29.10.18.
 */
@Service
@TbCoreComponent
@Slf4j
public class DefaultDeviceSessionCacheService implements DeviceSessionCacheService {

    @Autowired
    protected TbTransactionalCache<DeviceId, DeviceSessionsCacheEntry> cache;

    @Override
    public DeviceSessionsCacheEntry get(DeviceId deviceId) {
        log.debug("[{}] Fetching session data from cache", deviceId);
        return cache.getAndPutInTransaction(deviceId, () ->
                DeviceSessionsCacheEntry.newBuilder().addAllSessions(Collections.emptyList()).build(), false);
    }

    @Override
    public DeviceSessionsCacheEntry put(DeviceId deviceId, DeviceSessionsCacheEntry sessions) {
        log.debug("[{}] Pushing session data to cache: {}", deviceId, sessions);
        cache.putIfAbsent(deviceId, sessions);
        return sessions;
    }
}
