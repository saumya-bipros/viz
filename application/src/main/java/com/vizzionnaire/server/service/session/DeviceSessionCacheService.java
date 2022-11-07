package com.vizzionnaire.server.service.session;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.gen.transport.TransportProtos.DeviceSessionsCacheEntry;

/**
 * Created by ashvayka on 29.10.18.
 */
public interface DeviceSessionCacheService {

    DeviceSessionsCacheEntry get(DeviceId deviceId);

    DeviceSessionsCacheEntry put(DeviceId deviceId, DeviceSessionsCacheEntry sessions);

}
