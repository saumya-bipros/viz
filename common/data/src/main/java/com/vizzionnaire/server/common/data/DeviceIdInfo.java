package com.vizzionnaire.server.common.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.UUID;

import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;

@Data
@Slf4j
public class DeviceIdInfo implements Serializable, HasTenantId {

    private static final long serialVersionUID = 2233745129677581815L;

    private final TenantId tenantId;
    private final CustomerId customerId;
    private final DeviceId deviceId;

    public DeviceIdInfo(UUID tenantId, UUID customerId, UUID deviceId) {
        this.tenantId = new TenantId(tenantId);
        this.customerId = customerId != null ? new CustomerId(customerId) : null;
        this.deviceId = new DeviceId(deviceId);
    }
}
