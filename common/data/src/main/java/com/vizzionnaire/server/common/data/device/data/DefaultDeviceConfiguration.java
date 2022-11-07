package com.vizzionnaire.server.common.data.device.data;

import com.vizzionnaire.server.common.data.DeviceProfileType;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel
@Data
public class DefaultDeviceConfiguration implements DeviceConfiguration {

    @Override
    public DeviceProfileType getType() {
        return DeviceProfileType.DEFAULT;
    }

}
