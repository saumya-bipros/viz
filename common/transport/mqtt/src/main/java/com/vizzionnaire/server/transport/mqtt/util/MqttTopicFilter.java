package com.vizzionnaire.server.transport.mqtt.util;

public interface MqttTopicFilter {

    boolean filter(String topic);

}
