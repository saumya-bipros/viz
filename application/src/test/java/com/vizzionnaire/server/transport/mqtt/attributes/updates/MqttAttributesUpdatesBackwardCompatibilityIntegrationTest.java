package com.vizzionnaire.server.transport.mqtt.attributes.updates;

import lombok.extern.slf4j.Slf4j;

import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_SHORT_JSON_TOPIC;
import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_SHORT_PROTO_TOPIC;
import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_SHORT_TOPIC;
import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_ATTRIBUTES_TOPIC;

import org.junit.Test;

import com.vizzionnaire.server.common.data.TransportPayloadType;
import com.vizzionnaire.server.dao.service.DaoSqlTest;
import com.vizzionnaire.server.transport.mqtt.MqttTestConfigProperties;
import com.vizzionnaire.server.transport.mqtt.attributes.AbstractMqttAttributesIntegrationTest;

@Slf4j
@DaoSqlTest
public class MqttAttributesUpdatesBackwardCompatibilityIntegrationTest extends AbstractMqttAttributesIntegrationTest {

    @Test
    public void testSubscribeToAttributesUpdatesFromServerWithEnabledJsonCompatibility() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .enableCompatibilityWithJsonPayloadFormat(true)
                .build();
        processBeforeTest(configProperties);
        processProtoTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_TOPIC);
    }

    @Test
    public void testSubscribeToAttributesUpdatesFromServerWithEnabledJsonCompatibilityAndJsonDownlinks() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .enableCompatibilityWithJsonPayloadFormat(true)
                .useJsonPayloadFormatForDefaultDownlinkTopics(true)
                .build();
        super.processBeforeTest(configProperties);
        processJsonTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerOnShortTopicWithEnabledJsonCompatibilityAndJsonDownlinks() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .enableCompatibilityWithJsonPayloadFormat(true)
                .useJsonPayloadFormatForDefaultDownlinkTopics(true)
                .build();
        super.processBeforeTest(configProperties);
        processProtoTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerOnShortJsonTopicWithEnabledJsonCompatibilityAndJsonDownlinks() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .enableCompatibilityWithJsonPayloadFormat(true)
                .useJsonPayloadFormatForDefaultDownlinkTopics(true)
                .build();
        super.processBeforeTest(configProperties);
        processJsonTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_JSON_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerOnShortProtoTopicWithEnabledJsonCompatibilityAndJsonDownlinks() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .enableCompatibilityWithJsonPayloadFormat(true)
                .useJsonPayloadFormatForDefaultDownlinkTopics(true)
                .build();
        super.processBeforeTest(configProperties);
        processProtoTestSubscribeToAttributesUpdates(DEVICE_ATTRIBUTES_SHORT_PROTO_TOPIC);
    }

    @Test
    public void testProtoSubscribeToAttributesUpdatesFromTheServerGatewayWithEnabledJsonCompatibilityAndJsonDownlinks() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .gatewayName("Gateway Test Subscribe to attribute updates")
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .enableCompatibilityWithJsonPayloadFormat(true)
                .build();
        super.processBeforeTest(configProperties);
        processProtoGatewayTestSubscribeToAttributesUpdates();
    }

}
