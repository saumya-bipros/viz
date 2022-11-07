package com.vizzionnaire.server.common.transport.auth;

import java.util.Optional;

import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.security.DeviceCredentialsFilter;

public interface DeviceAuthService {

    DeviceAuthResult process(DeviceCredentialsFilter credentials);

}
