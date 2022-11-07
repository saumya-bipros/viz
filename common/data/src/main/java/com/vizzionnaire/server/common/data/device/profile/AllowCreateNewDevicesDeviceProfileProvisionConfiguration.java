package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.DeviceProfileProvisionType;

import lombok.Data;

@Data
public class AllowCreateNewDevicesDeviceProfileProvisionConfiguration implements DeviceProfileProvisionConfiguration {

    private final String provisionDeviceSecret;

    @Override
    public DeviceProfileProvisionType getType() {
        return DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES;
    }

}
