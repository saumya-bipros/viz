package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.CoapDeviceType;

import lombok.Data;

@Data
public class DefaultCoapDeviceTypeConfiguration implements CoapDeviceTypeConfiguration {

    private TransportPayloadTypeConfiguration transportPayloadTypeConfiguration;

    @Override
    public CoapDeviceType getCoapDeviceType() {
        return CoapDeviceType.DEFAULT;
    }

    public TransportPayloadTypeConfiguration getTransportPayloadTypeConfiguration() {
        if (transportPayloadTypeConfiguration != null) {
            return transportPayloadTypeConfiguration;
        } else {
            return new JsonTransportPayloadConfiguration();
        }
    }

}
