package com.vizzionnaire.server.transport.mqtt.attributes.updates;

import lombok.extern.slf4j.Slf4j;

import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_SHORT_JSON_TOPIC;
import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_SHORT_PROTO_TOPIC;
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
public class MqttAttributesUpdatesProtoIntegrationTest extends AbstractMqttAttributesIntegrationTest {

    @Before
    public void beforeTest() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Subscribe to attribute updates")
                .gatewayName("Gateway Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .build();
        processBeforeTest(configProperties);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServer() throws Exception {
        processProtoTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerOnShortTopic() throws Exception {
        processProtoTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerOnShortJsonTopic() throws Exception {
        processJsonTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_JSON_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerOnShortProtoTopic() throws Exception {
        processProtoTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_PROTO_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerGateway() throws Exception {
        processProtoGatewayTestSubscribeToAttributesUpdates();
    }

}
