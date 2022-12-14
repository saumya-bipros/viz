package com.vizzionnaire.rule.engine.mqtt;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.rule.engine.credentials.AnonymousCredentials;
import com.vizzionnaire.rule.engine.credentials.ClientCredentials;

@Data
public class TbMqttNodeConfiguration implements NodeConfiguration<TbMqttNodeConfiguration> {

    private String topicPattern;
    private String host;
    private int port;
    private int connectTimeoutSec;
    private String clientId;
    private boolean appendClientIdSuffix;

    private boolean cleanSession;
    private boolean ssl;
    private ClientCredentials credentials;

    @Override
    public TbMqttNodeConfiguration defaultConfiguration() {
        TbMqttNodeConfiguration configuration = new TbMqttNodeConfiguration();
        configuration.setTopicPattern("my-topic");
        configuration.setPort(1883);
        configuration.setConnectTimeoutSec(10);
        configuration.setCleanSession(true);
        configuration.setSsl(false);
        configuration.setCredentials(new AnonymousCredentials());
        return configuration;
    }

}
