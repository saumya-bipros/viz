package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.DeviceProfileProvisionType;

import lombok.Data;

@Data
public class CheckPreProvisionedDevicesDeviceProfileProvisionConfiguration implements DeviceProfileProvisionConfiguration {

    private final String provisionDeviceSecret;

    @Override
    public DeviceProfileProvisionType getType() {
        return DeviceProfileProvisionType.CHECK_PRE_PROVISIONED_DEVICES;
    }

}
