package com.vizzionnaire.server.transport.mqtt;

import com.vizzionnaire.server.common.data.DeviceProfileProvisionType;
import com.vizzionnaire.server.common.data.TransportPayloadType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqttTestConfigProperties {

    String deviceName;
    String gatewayName;

    TransportPayloadType transportPayloadType;

    String telemetryTopicFilter;
    String attributesTopicFilter;

    String telemetryProtoSchema;
    String attributesProtoSchema;
    String rpcResponseProtoSchema;
    String rpcRequestProtoSchema;

    boolean enableCompatibilityWithJsonPayloadFormat;
    boolean useJsonPayloadFormatForDefaultDownlinkTopics;
    boolean sendAckOnValidationException;

    DeviceProfileProvisionType provisionType;
    String provisionKey;
    String provisionSecret;

}
