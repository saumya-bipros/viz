package com.vizzionnaire.server.common.transport;

import com.vizzionnaire.server.common.data.Device;

import lombok.Getter;

@Getter
public class DeviceUpdatedEvent {
    private final Device device;

    public DeviceUpdatedEvent(Device device) {
        this.device = device;
    }
}
