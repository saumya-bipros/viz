package com.vizzionnaire.server.common.transport.auth;

import lombok.Data;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.device.data.PowerMode;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.TenantId;

@Data
public class TransportDeviceInfo implements Serializable {

    private TenantId tenantId;
    private CustomerId customerId;
    private DeviceProfileId deviceProfileId;
    private DeviceId deviceId;
    private String deviceName;
    private String deviceType;
    private PowerMode powerMode;
    private String additionalInfo;
    private Long edrxCycle;
    private Long psmActivityTimer;
    private Long pagingTransmissionWindow;
}
