package com.vizzionnaire.server.transport.coap.provision;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.californium.core.CoapResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vizzionnaire.server.common.data.CoapDeviceType;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfileProvisionType;
import com.vizzionnaire.server.common.data.TransportPayloadType;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.common.data.security.DeviceCredentialsType;
import com.vizzionnaire.server.common.msg.EncryptionUtil;
import com.vizzionnaire.server.common.msg.session.FeatureType;
import com.vizzionnaire.server.dao.device.DeviceCredentialsService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.device.provision.ProvisionResponseStatus;
import com.vizzionnaire.server.dao.service.DaoSqlTest;
import com.vizzionnaire.server.gen.transport.TransportProtos.CredentialsDataProto;
import com.vizzionnaire.server.gen.transport.TransportProtos.CredentialsType;
import com.vizzionnaire.server.gen.transport.TransportProtos.ProvisionDeviceCredentialsMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ProvisionDeviceRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ProvisionDeviceResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ValidateDeviceTokenRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ValidateDeviceX509CertRequestMsg;
import com.vizzionnaire.server.transport.coap.AbstractCoapIntegrationTest;
import com.vizzionnaire.server.transport.coap.CoapTestClient;
import com.vizzionnaire.server.transport.coap.CoapTestConfigProperties;

@Slf4j
@DaoSqlTest
public class CoapProvisionProtoDeviceTest extends AbstractCoapIntegrationTest {

    @Autowired
    DeviceCredentialsService deviceCredentialsService;

    @Autowired
    DeviceService deviceService;

    @After
    public void afterTest() throws Exception {
        processAfterTest();
    }

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
    public void testProvisioningWithBadKeyDevice() throws Exception {
        processTestProvisioningWithBadKeyDevice();
    }


    private void processTestProvisioningDisabledDevice() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Provision device")
                .coapDeviceType(CoapDeviceType.DEFAULT)
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .build();
        processBeforeTest(configProperties);
        ProvisionDeviceResponseMsg result = ProvisionDeviceResponseMsg.parseFrom(createCoapClientAndPublish());
        Assert.assertNotNull(result);
        Assert.assertEquals(ProvisionResponseStatus.NOT_FOUND.name(), result.getStatus().name());
    }

    private void processTestProvisioningCreateNewDeviceWithoutCredentials() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Provision device3")
                .coapDeviceType(CoapDeviceType.DEFAULT)
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .provisionType(DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        processBeforeTest(configProperties);
        ProvisionDeviceResponseMsg response = ProvisionDeviceResponseMsg.parseFrom(createCoapClientAndPublish());

        Device createdDevice = deviceService.findDeviceByTenantIdAndName(tenantId, "Test Provision device");

        Assert.assertNotNull(createdDevice);

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, createdDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.getCredentialsType().name());
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.getStatus().name());
    }

    private void processTestProvisioningCreateNewDeviceWithAccessToken() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Provision device3")
                .coapDeviceType(CoapDeviceType.DEFAULT)
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .provisionType(DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        processBeforeTest(configProperties);
        CredentialsDataProto requestCredentials = CredentialsDataProto.newBuilder()
                .setValidateDeviceTokenRequestMsg(ValidateDeviceTokenRequestMsg.newBuilder().setToken("test_token").build())
                .build();

        ProvisionDeviceResponseMsg response = ProvisionDeviceResponseMsg.parseFrom(
                createCoapClientAndPublish(createTestsProvisionMessage(CredentialsType.ACCESS_TOKEN, requestCredentials)));

        Device createdDevice = deviceService.findDeviceByTenantIdAndName(tenantId, "Test Provision device");

        Assert.assertNotNull(createdDevice);

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, createdDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.getCredentialsType().toString());
        Assert.assertEquals(deviceCredentials.getCredentialsType(), DeviceCredentialsType.ACCESS_TOKEN);
        Assert.assertEquals(deviceCredentials.getCredentialsId(), "test_token");
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.getStatus().toString());
    }

    private void processTestProvisioningCreateNewDeviceWithCert() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Provision device3")
                .coapDeviceType(CoapDeviceType.DEFAULT)
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .provisionType(DeviceProfileProvisionType.ALLOW_CREATE_NEW_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        processBeforeTest(configProperties);
        CredentialsDataProto requestCredentials = CredentialsDataProto.newBuilder()
                .setValidateDeviceX509CertRequestMsg(
                        ValidateDeviceX509CertRequestMsg.newBuilder().setHash("testHash").build())
                .build();

        ProvisionDeviceResponseMsg response = ProvisionDeviceResponseMsg.parseFrom(
                createCoapClientAndPublish(createTestsProvisionMessage(CredentialsType.X509_CERTIFICATE, requestCredentials)));

        Device createdDevice = deviceService.findDeviceByTenantIdAndName(tenantId, "Test Provision device");

        Assert.assertNotNull(createdDevice);

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, createdDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.getCredentialsType().toString());
        Assert.assertEquals(deviceCredentials.getCredentialsType(), DeviceCredentialsType.X509_CERTIFICATE);

        String cert = EncryptionUtil.certTrimNewLines(deviceCredentials.getCredentialsValue());
        String sha3Hash = EncryptionUtil.getSha3Hash(cert);

        Assert.assertEquals(deviceCredentials.getCredentialsId(), sha3Hash);

        Assert.assertEquals(deviceCredentials.getCredentialsValue(), "testHash");
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.getStatus().toString());
    }

    private void processTestProvisioningCheckPreProvisionedDevice() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Provision device")
                .coapDeviceType(CoapDeviceType.DEFAULT)
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .provisionType(DeviceProfileProvisionType.CHECK_PRE_PROVISIONED_DEVICES)
                .provisionKey("testProvisionKey")
                .provisionSecret("testProvisionSecret")
                .build();
        processBeforeTest(configProperties);
        ProvisionDeviceResponseMsg response = ProvisionDeviceResponseMsg.parseFrom(createCoapClientAndPublish());

        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(tenantId, savedDevice.getId());

        Assert.assertEquals(deviceCredentials.getCredentialsType().name(), response.getCredentialsType().name());
        Assert.assertEquals(ProvisionResponseStatus.SUCCESS.name(), response.getStatus().name());
    }

    private void processTestProvisioningWithBadKeyDevice() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Provision device")
                .coapDeviceType(CoapDeviceType.DEFAULT)
                .transportPayloadType(TransportPayloadType.PROTOBUF)
                .provisionType(DeviceProfileProvisionType.CHECK_PRE_PROVISIONED_DEVICES)
                .provisionKey("testProvisionKeyOrig")
                .provisionSecret("testProvisionSecret")
                .build();
        processBeforeTest(configProperties);
        ProvisionDeviceResponseMsg response = ProvisionDeviceResponseMsg.parseFrom(createCoapClientAndPublish());
        Assert.assertEquals(ProvisionResponseStatus.NOT_FOUND.name(), response.getStatus().name());
    }

    private byte[] createCoapClientAndPublish() throws Exception {
        return createCoapClientAndPublish(createTestProvisionMessage());
    }

    private byte[] createCoapClientAndPublish(byte[] provisionRequestMsg) throws Exception {
        client = new CoapTestClient(accessToken, FeatureType.PROVISION);
        CoapResponse coapResponse = client.postMethod(provisionRequestMsg);
        Assert.assertNotNull("COAP response", coapResponse);
        return coapResponse.getPayload();
    }

    private byte[] createTestsProvisionMessage(CredentialsType credentialsType, CredentialsDataProto credentialsData) throws Exception {
        return ProvisionDeviceRequestMsg.newBuilder()
                .setDeviceName("Test Provision device")
                .setCredentialsType(credentialsType != null ? credentialsType : CredentialsType.ACCESS_TOKEN)
                .setCredentialsDataProto(credentialsData != null ? credentialsData: CredentialsDataProto.newBuilder().build())
                .setProvisionDeviceCredentialsMsg(
                        ProvisionDeviceCredentialsMsg.newBuilder()
                                .setProvisionDeviceKey("testProvisionKey")
                                .setProvisionDeviceSecret("testProvisionSecret")
                ).build()
                .toByteArray();
    }

    private byte[] createTestProvisionMessage() throws Exception {
        return createTestsProvisionMessage(null, null);
    }

}
