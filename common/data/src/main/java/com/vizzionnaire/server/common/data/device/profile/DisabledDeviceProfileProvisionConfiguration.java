package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.DeviceProfileProvisionType;

import lombok.Data;

@Data
public class DisabledDeviceProfileProvisionConfiguration implements DeviceProfileProvisionConfiguration {

    private final String provisionDeviceSecret;

    @Override
    public DeviceProfileProvisionType getType() {
        return DeviceProfileProvisionType.DISABLED;
    }

}
