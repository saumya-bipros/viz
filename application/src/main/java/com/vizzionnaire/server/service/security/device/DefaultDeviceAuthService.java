package com.vizzionnaire.server.service.security.device;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.common.data.security.DeviceCredentialsFilter;
import com.vizzionnaire.server.common.transport.auth.DeviceAuthResult;
import com.vizzionnaire.server.common.transport.auth.DeviceAuthService;
import com.vizzionnaire.server.dao.device.DeviceCredentialsService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Service
@TbCoreComponent
@Slf4j
public class DefaultDeviceAuthService implements DeviceAuthService {

    private final DeviceService deviceService;

    private final DeviceCredentialsService deviceCredentialsService;

    public DefaultDeviceAuthService(DeviceService deviceService, DeviceCredentialsService deviceCredentialsService) {
        this.deviceService = deviceService;
        this.deviceCredentialsService = deviceCredentialsService;
    }

    @Override
    public DeviceAuthResult process(DeviceCredentialsFilter credentialsFilter) {
        log.trace("Lookup device credentials using filter {}", credentialsFilter);
        DeviceCredentials credentials = deviceCredentialsService.findDeviceCredentialsByCredentialsId(credentialsFilter.getCredentialsId());
        if (credentials != null) {
            log.trace("Credentials found {}", credentials);
            if (credentials.getCredentialsType() == credentialsFilter.getCredentialsType()) {
                switch (credentials.getCredentialsType()) {
                    case ACCESS_TOKEN:
                        // Credentials ID matches Credentials value in this
                        // primitive case;
                        return DeviceAuthResult.of(credentials.getDeviceId());
                    case X509_CERTIFICATE:
                        return DeviceAuthResult.of(credentials.getDeviceId());
                    case LWM2M_CREDENTIALS:
                        return DeviceAuthResult.of(credentials.getDeviceId());
                    default:
                        return DeviceAuthResult.of("Credentials Type is not supported yet!");
                }
            } else {
                return DeviceAuthResult.of("Credentials Type mismatch!");
            }
        } else {
            log.trace("Credentials not found!");
            return DeviceAuthResult.of("Credentials Not Found!");
        }
    }

}
