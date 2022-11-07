package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.TransportPayloadType;

import lombok.Data;

@Data
public class JsonTransportPayloadConfiguration implements TransportPayloadTypeConfiguration {

    @Override
    public TransportPayloadType getTransportPayloadType() {
        return TransportPayloadType.JSON;
    }
}
