package com.vizzionnaire.server.common.transport.auth;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.DeviceProfile;

@Data
@Builder
public class ValidateDeviceCredentialsResponse implements DeviceProfileAware, Serializable {

    private final TransportDeviceInfo deviceInfo;
    private final DeviceProfile deviceProfile;
    private final String credentials;

    public boolean hasDeviceInfo() {
        return deviceInfo != null;
    }
}
