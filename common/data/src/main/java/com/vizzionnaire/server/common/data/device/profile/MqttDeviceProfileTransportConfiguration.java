package com.vizzionnaire.server.common.data.device.profile;

import com.vizzionnaire.server.common.data.DeviceTransportType;
import com.vizzionnaire.server.common.data.validation.NoXss;

import lombok.Data;

@Data
public class MqttDeviceProfileTransportConfiguration implements DeviceProfileTransportConfiguration {

    @NoXss
    private String deviceTelemetryTopic = MqttTopics.DEVICE_TELEMETRY_TOPIC;
    @NoXss
    private String deviceAttributesTopic = MqttTopics.DEVICE_ATTRIBUTES_TOPIC;
    private TransportPayloadTypeConfiguration transportPayloadTypeConfiguration;
    private boolean sendAckOnValidationException;

    @Override
    public DeviceTransportType getType() {
        return DeviceTransportType.MQTT;
    }

    public TransportPayloadTypeConfiguration getTransportPayloadTypeConfiguration() {
        if (transportPayloadTypeConfiguration != null) {
            return transportPayloadTypeConfiguration;
        } else {
            return new JsonTransportPayloadConfiguration();
        }
    }


}
