package com.vizzionnaire.server.transport.mqtt.provision;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfileProvisionType;
import com.vizzionnaire.server.common.data.TransportPayloadType;
import com.vizzionnaire.server.common.data.device.credentials.BasicMqttCredentials;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.common.msg.EncryptionUtil;
import com.vizzionnaire.server.dao.device.DeviceCredentialsService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.device.provision.ProvisionResponseStatus;
import com.vizzionnaire.server.dao.service.DaoSqlTest;
import com.vizzionnaire.server.transport.mqtt.AbstractMqttIntegrationTest;
import com.vizzionnaire.server.transport.mqtt.MqttTestCallback;
import com.vizzionnaire.server.transport.mqtt.MqttTestClient;
import com.vizzionnaire.server.transport.mqtt.MqttTestConfigProperties;

import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_PROVISION_REQUEST_TOPIC;
import static com.vizzionnaire.server.common.data.device.profile.MqttTopics.DEVICE_PROVISION_RESPONSE_TOPIC;

import java.util.concurrent.TimeUnit;

@Slf4j
@DaoSqlTest
public class MqttProvisionJsonDeviceTest extends AbstractMqttIntegrationTest {

    @Autowired
    DeviceCredentialsService deviceCredentialsService;

    @Autowired
    DeviceService deviceService;

    @Test
    public void testProvisioningDisabledDevice() throws Exception {
        processTestProvisioningDisabledDevice();
    }

    @Test
    public void testProvisioningCheckPreProvisionedDevice() throws Exception {
        processTestProvisioningCheckPreProvisionedDevice();
    }

    @Test
    public void testProvisioningCreateNewDeviceWithoutCredentials() throws Exception {
        processTestProvisioningCreateNewDeviceWithoutCredentials();
    }

    @Test
    public void testProvisioningCreateNewDeviceWithAccessToken() throws Exception {
        processTestProvisioningCreateNewDeviceWithAccessToken();
    }

    @Test
    public void testProvisioningCreateNewDeviceWithCert() throws Exception {
        processTestProvisioningCreateNewDeviceWithCert();
    }

    @Test
    public void testProvisioningCreateNewDeviceWithMqttBasic() throws Exception {
        processTestProvisioningCreateNewDeviceWithMqttBasic();
    }

    @Test
    public void testProvisioningWithBadKeyDevice() throws Exception {
        processTestProvisioningWithBadKeyDevice();
    }


    protected void processTestProvisioningDisabledDevice() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Provision device")
                .transportPayloadType(TransportPayloadType.JSON)
                .provisionType(DeviceProfileProvisionType.DISABLED)
                .build();
        super.processBeforeTest(configProperties);
        byte[] result = createMqttClientAndPublish();
        JsonNode response = JacksonUtil.fromBytes(result);
        Assert.assertTrue(response.hasNonNull("errorMsg"));
        Assert.assertTrue(response.hasNonNull("status"));
        Assert.assertEquals("Provision data was not found!", response.get("errorMsg").asText());
        Assert.assertEquals(ProvisionResponseStatus.NOT_FOUND.name(), response.get("status").asText());
    }


    protected void processTestProvisioningCreateNewDeviceWithoutCredentials() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Provision device3")
                .transportPayloadType(TransportPayloadType.JSON)
                .provisionType(DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        super.processBeforeTest(configProperties);
        byte[] result = createMqttClientAndPublish();
        JsonNode response = JacksonUtil.fromBytes(result);
        Assert.assertTrue(response.hasNonNull("credentialsType"));
        Assert.assertTrue(response.hasNonNull("status"));

        Device createdDevice = deviceService.findDeviceByTenantIdAndName(tenantId, "Test Provision device");

        Assert.assertNotNull(createdDevice);

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, createdDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.get("credentialsType").asText());
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.get("status").asText());
    }


    protected void processTestProvisioningCreateNewDeviceWithAccessToken() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Provision device3")
                .transportPayloadType(TransportPayloadType.JSON)
                .provisionType(DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        super.processBeforeTest(configProperties);
        String requestCredentials = ",\"credentialsType\": \"ACCESS_TOKEN\",\"token\": \"test_token\"";
        byte[] result = createMqttClientAndPublish(requestCredentials);
        JsonNode response = JacksonUtil.fromBytes(result);
        Assert.assertTrue(response.hasNonNull("credentialsType"));
        Assert.assertTrue(response.hasNonNull("status"));

        Device createdDevice = deviceService.findDeviceByTenantIdAndName(tenantId, "Test Provision device");

        Assert.assertNotNull(createdDevice);

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, createdDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.get("credentialsType").asText());
        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), "ACCESS_TOKEN");
        Assert.assertEquals(deviceCredentials.getCredentialsId(), "test_token");
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.get("status").asText());
    }


    protected void processTestProvisioningCreateNewDeviceWithCert() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Provision device3")
                .transportPayloadType(TransportPayloadType.JSON)
                .provisionType(DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        super.processBeforeTest(configProperties);
        String requestCredentials = ",\"credentialsType\": \"X509_CERTIFICATE\",\"hash\": \"testHash\"";
        byte[] result = createMqttClientAndPublish(requestCredentials);
        JsonNode response = JacksonUtil.fromBytes(result);
        Assert.assertTrue(response.hasNonNull("credentialsType"));
        Assert.assertTrue(response.hasNonNull("status"));

        Device createdDevice = deviceService.findDeviceByTenantIdAndName(tenantId, "Test Provision device");

        Assert.assertNotNull(createdDevice);

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, createdDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.get("credentialsType").asText());
        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), "X509_CERTIFICATE");

        String cert = EncryptionUtil.certTrimNewLines(deviceCredentials.getCredentialsValue());
        String sha3Hash = EncryptionUtil.getSha3Hash(cert);

        Assert.assertEquals(deviceCredentials.getCredentialsId(), sha3Hash);

        Assert.assertEquals(deviceCredentials.getCredentialsValue(), "testHash");
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.get("status").asText());
    }


    protected void processTestProvisioningCreateNewDeviceWithMqttBasic() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Provision device3")
                .transportPayloadType(TransportPayloadType.JSON)
                .provisionType(DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        super.processBeforeTest(configProperties);
        String requestCredentials = ",\"credentialsType\": \"MQTT_BASIC\",\"clientId\": \"test_clientId\",\"username\": \"test_username\",\"password\": \"test_password\"";
        byte[] result = createMqttClientAndPublish(requestCredentials);
        JsonNode response = JacksonUtil.fromBytes(result);
        Assert.assertTrue(response.hasNonNull("credentialsType"));
        Assert.assertTrue(response.hasNonNull("status"));

        Device createdDevice = deviceService.findDeviceByTenantIdAndName(tenantId, "Test Provision device");

        Assert.assertNotNull(createdDevice);

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, createdDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.get("credentialsType").asText());
        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), "MQTT_BASIC");
        Assert.assertEquals(deviceCredentials.getCredentialsId(), EncryptionUtil.getSha3Hash("|", "test_clientId", "test_username"));

        BasicMqttCredentials mqttCredentials = new BasicMqttCredentials();
        mqttCredentials.setClientId("test_clientId");
        mqttCredentials.setUserName("test_username");
        mqttCredentials.setPassword("test_password");

        Assert.assertEquals(deviceCredentials.getCredentialsValue(), JacksonUtil.toString(mqttCredentials));
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.get("status").asText());
    }

    protected void processTestProvisioningCheckPreProvisionedDevice() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Provision device")
                .transportPayloadType(TransportPayloadType.JSON)
                .provisionType(DeviceProfileProvisionType.CHECK_PRE_PROVISIONED_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        super.processBeforeTest(configProperties);
        byte[] result = createMqttClientAndPublish();
        JsonNode response = JacksonUtil.fromBytes(result);
        Assert.assertTrue(response.hasNonNull("credentialsType"));
        Assert.assertTrue(response.hasNonNull("status"));

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, savedDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.get("credentialsType").asText());
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.get("status").asText());
    }

    protected void processTestProvisioningWithBadKeyDevice() throws Exception {
        MqttTestConfigProperties configProperties = MqttTestConfigProperties.builder()
                .deviceName("Test Provision device")
                .transportPayloadType(TransportPayloadType.JSON)
                .provisionType(DeviceProfileProvisionType.CHECK_PRE_PROVISIONED_DEVICES)
                .provisionKey("testProvisionKeyOrig")
                .provisionSecret("testProvisionSecret")
                .build();
        super.processBeforeTest(configProperties);
        byte[] result = createMqttClientAndPublish();
        JsonNode response = JacksonUtil.fromBytes(result);
        Assert.assertTrue(response.hasNonNull("errorMsg"));
        Assert.assertTrue(response.hasNonNull("status"));
        Assert.assertEquals("Provision data was not found!", response.get("errorMsg").asText());
        Assert.assertEquals(ProvisionResponseStatus.NOT_FOUND.name(), response.get("status").asText());
    }

    protected byte[] createMqttClientAndPublish() throws Exception {
        return createMqttClientAndPublish("");
    }

    protected byte[] createMqttClientAndPublish(String deviceCredentials) throws Exception {
        String provisionRequestMsg = createTestProvisionMessage(deviceCredentials);
        MqttTestClient client = new MqttTestClient();
        client.connectAndWait("provision");
        MqttTestCallback onProvisionCallback = new MqttTestCallback(DEVICE_PROVISION_RESPONSE_TOPIC);
        client.setCallback(onProvisionCallback);
        client.subscribe(DEVICE_PROVISION_RESPONSE_TOPIC, MqttQoS.AT_MOST_ONCE);
        client.publishAndWait(DEVICE_PROVISION_REQUEST_TOPIC, provisionRequestMsg.getBytes());
        onProvisionCallback.getSubscribeLatch().await(3, TimeUnit.SECONDS);
        client.disconnect();
        return onProvisionCallback.getPayloadBytes();
    }

    protected String createTestProvisionMessage(String deviceCredentials) {
        return "{\"deviceName\":\"Test Provision device\",\"provisionDeviceKey\":\"testProvisionKey\", \"provisionDeviceSecret\":\"testProvisionSecret\"" + deviceCredentials + "}";
    }
}
