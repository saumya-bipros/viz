package com.vizzionnaire.server.common.transport;

import lombok.Getter;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.queue.discovery.event.TbApplicationEvent;

public final class DeviceProfileUpdatedEvent extends TbApplicationEvent {

    @Getter
    private final DeviceProfile deviceProfile;

    public DeviceProfileUpdatedEvent(DeviceProfile deviceProfile) {
        super(new Object());
        this.deviceProfile = deviceProfile;
    }
}
