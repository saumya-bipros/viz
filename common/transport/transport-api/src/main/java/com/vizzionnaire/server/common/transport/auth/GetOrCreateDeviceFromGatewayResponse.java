package com.vizzionnaire.server.common.transport.auth;

import com.vizzionnaire.server.common.data.DeviceProfile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOrCreateDeviceFromGatewayResponse implements DeviceProfileAware {

    private TransportDeviceInfo deviceInfo;
    private DeviceProfile deviceProfile;

}
