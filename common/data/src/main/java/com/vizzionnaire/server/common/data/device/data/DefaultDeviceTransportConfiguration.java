package com.vizzionnaire.server.common.data.device.data;

import com.vizzionnaire.server.common.data.DeviceProfileType;
import com.vizzionnaire.server.common.data.DeviceTransportType;

import lombok.Data;

@Data
public class DefaultDeviceTransportConfiguration implements DeviceTransportConfiguration {

    @Override
    public DeviceTransportType getType() {
        return DeviceTransportType.DEFAULT;
    }

}
