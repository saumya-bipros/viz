package com.vizzionnaire.server.cache.device;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class DeviceCacheKey implements Serializable {

    private final TenantId tenantId;
    private final DeviceId deviceId;
    private final String deviceName;

    public DeviceCacheKey(TenantId tenantId, DeviceId deviceId) {
        this(tenantId, deviceId, null);
    }

    public DeviceCacheKey(TenantId tenantId, String deviceName) {
        this(tenantId, null, deviceName);
    }

    @Override
    public String toString() {
        if (deviceId != null) {
            return tenantId + "_" + deviceId;
        } else {
            return tenantId + "_n_" + deviceName;
        }
    }

}
