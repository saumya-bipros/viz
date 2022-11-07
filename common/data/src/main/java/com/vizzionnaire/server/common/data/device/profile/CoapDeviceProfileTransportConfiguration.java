package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.DeviceTransportType;
import com.vizzionnaire.server.common.data.device.data.PowerSavingConfiguration;

import lombok.Data;

@Data
public class CoapDeviceProfileTransportConfiguration implements DeviceProfileTransportConfiguration {

    private CoapDeviceTypeConfiguration coapDeviceTypeConfiguration;
    private PowerSavingConfiguration clientSettings;

    @Override
    public DeviceTransportType getType() {
        return DeviceTransportType.COAP;
    }

    public CoapDeviceTypeConfiguration getCoapDeviceTypeConfiguration() {
        if (coapDeviceTypeConfiguration != null) {
            return coapDeviceTypeConfiguration;
        } else {
            return new DefaultCoapDeviceTypeConfiguration();
        }
    }
}