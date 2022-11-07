package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.DeviceProfileType;
import com.vizzionnaire.server.common.data.DeviceTransportType;

import lombok.Data;

@Data
public class DefaultDeviceProfileTransportConfiguration implements DeviceProfileTransportConfiguration {

    @Override
    public DeviceTransportType getType() {
        return DeviceTransportType.DEFAULT;
    }

}
