package com.vizzionnaire.server.dao.device;

import com.vizzionnaire.server.dao.device.provision.ProvisionFailedException;
import com.vizzionnaire.server.dao.device.provision.ProvisionRequest;
import com.vizzionnaire.server.dao.device.provision.ProvisionResponse;

public interface DeviceProvisionService {

    ProvisionResponse provisionDevice(ProvisionRequest provisionRequest) throws ProvisionFailedException;
}
