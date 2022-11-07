package com.vizzionnaire.server.dao.device.provision;

import com.vizzionnaire.server.common.data.security.DeviceCredentials;

import lombok.Data;

@Data
public class ProvisionResponse {
    private final DeviceCredentials deviceCredentials;
    private final ProvisionResponseStatus responseStatus;
}
