package com.vizzionnaire.server.transport.coap;

import com.vizzionnaire.server.common.data.CoapDeviceType;
import com.vizzionnaire.server.common.data.DeviceProfileProvisionType;
import com.vizzionnaire.server.common.data.TransportPayloadType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoapTestConfigProperties {

    String deviceName;

    CoapDeviceType coapDeviceType;

    TransportPayloadType transportPayloadType;

    String telemetryTopicFilter;
    String attributesTopicFilter;

    String telemetryProtoSchema;
    String attributesProtoSchema;
    String rpcResponseProtoSchema;
    String rpcRequestProtoSchema;

    DeviceProfileProvisionType provisionType;
    String provisionKey;
    String provisionSecret;

}
