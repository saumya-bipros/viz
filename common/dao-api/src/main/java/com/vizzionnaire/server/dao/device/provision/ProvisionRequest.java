package com.vizzionnaire.server.dao.device.provision;

import com.vizzionnaire.server.common.data.device.credentials.ProvisionDeviceCredentialsData;
import com.vizzionnaire.server.common.data.device.profile.ProvisionDeviceProfileCredentials;
import com.vizzionnaire.server.common.data.security.DeviceCredentialsType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProvisionRequest {
    private String deviceName;
    private DeviceCredentialsType credentialsType;
    private ProvisionDeviceCredentialsData credentialsData;
    private ProvisionDeviceProfileCredentials credentials;
}
