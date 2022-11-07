package com.vizzionnaire.server.service.gateway_device;

import com.vizzionnaire.server.common.data.Device;

public interface GatewayNotificationsService {

    void onDeviceUpdated(Device device, Device oldDevice);

    void onDeviceDeleted(Device device);
}
