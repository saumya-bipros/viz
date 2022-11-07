package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.DeviceProfileType;

import lombok.Data;

@Data
public class DefaultDeviceProfileConfiguration implements DeviceProfileConfiguration {

    @Override
    public DeviceProfileType getType() {
        return DeviceProfileType.DEFAULT;
    }

}
