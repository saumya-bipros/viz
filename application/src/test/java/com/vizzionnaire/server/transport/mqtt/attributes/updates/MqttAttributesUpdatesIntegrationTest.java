package com.vizzionnaire.server.transport.mqtt.attributes.updates;

import lombok.extern.slf4j.Slf4j;

import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_SHORT_JSON_TOPIC;
import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_SHORT_TOPIC;
import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_TOPIC;

import org.junit.Before;
import org.junit.Test;

import com.vizzionnaire.server.common.data.TransportPayloadType;
import com.vizzionnaire.server.dao.service.DaoSqlTest;
import com.vizzionnaire.server.transport.mqtt.MqttTestConfigProperties;
import com.vizzionnaire.server.transport.mqtt.attributes.AbstractMqttAttributesIntegrationTest;

@Slf4j
@DaoSqlTest
public class MqttAttributesUpdatesIntegrationTest extends AbstractMqttAttributesIntegrationTest {

    @Before
    public void beforeTest() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Subscribe to attribute updates")
                .gatewayName("Gateway Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.JSON)
                .build();
        processBeforeTest(configProperties);
    }

    @Test
    public void testJsonSubscribeToAttributesUpdatesFromTheServer() throws Exception {
        processJsonTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_TOPIC);
    }

    @Test
    public void testJsonSubscribeToAttributesUpdatesFromTheServerOnShortTopic() throws Exception {
        processJsonTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_TOPIC);
    }

    @Test
    public void testJsonSubscribeToAttributesUpdatesFromTheServerOnShortJsonTopic() throws Exception {
        processJsonTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_JSON_TOPIC);
    }

    @Test
    public void testJsonSubscribeToAttributesUpdatesFromTheServerGateway() throws Exception {
        processJsonGatewayTestSubscribeToAttributesUpdates();
    }

}
