package com.vizzionnaire.rule.engine.mqtt.azure;

import com.vizzionnaire.rule.engine.mqtt.TbMqttNodeConfiguration;

import lombok.Data;

@Data
public class TbAzureIotHubNodeConfiguration extends TbMqttNodeConfiguration {

    @Override
    public TbAzureIotHubNodeConfiguration defaultConfiguration() {
        TbAzureIotHubNodeConfiguration configuration = new TbAzureIotHubNodeConfiguration();
        configuration.setTopicPattern("devices/<device_id>/messages/events/");
        configuration.setHost("<iot-hub-name>.azure-devices.net");
        configuration.setPort(8883);
        configuration.setConnectTimeoutSec(10);
        configuration.setCleanSession(true);
        configuration.setSsl(true);
        configuration.setCredentials(new AzureIotHubSasCredentials());
        return configuration;
    }

}
