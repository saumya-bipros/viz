package com.vizzionnaire.server.msa.connectivity;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.msa.AbstractContainerTest;
import com.vizzionnaire.server.msa.WsClient;
import com.vizzionnaire.server.msa.mapper.WsTelemetryResponse;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.vizzionnaire.server.common.data.DataConstants.DEVICE;
import static com.vizzionnaire.server.common.data.DataConstants.SHARED_SCOPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HttpClientTest extends AbstractContainerTest {

    @Test
    public void telemetryUpload() throws Exception {
        restClient.login("tenant@vizzionnaire.org", "tenant");

        Device device = createDevice("http_");
        DeviceCredentials deviceCredentials = restClient.getDeviceCredentialsByDeviceId(device.getId()).get();

        WsClient wsClient = subscribeToWebSocket(device.getId(), "LATEST_TELEMETRY", CmdsType.TS_SUB_CMDS);
        ResponseEntity deviceTelemetryResponse = restClient.getRestTemplate()
                .postForEntity(HTTPS_URL + "/api/v1/{credentialsId}/telemetry",
                        mapper.readTree(createPayload().toString()),
                        ResponseEntity.class,
                        deviceCredentials.getCredentialsId());
        Assert.assertTrue(deviceTelemetryResponse.getStatusCode().is2xxSuccessful());
        WsTelemetryResponse actualLatestTelemetry = wsClient.getLastMessage();
        wsClient.closeBlocking();

        Assert.assertEquals(Sets.newHashSet("booleanKey", "stringKey", "doubleKey", "longKey"),
                actualLatestTelemetry.getLatestValues().keySet());

        Assert.assertTrue(verify(actualLatestTelemetry, "booleanKey", Boolean.TRUE.toString()));
        Assert.assertTrue(verify(actualLatestTelemetry, "stringKey", "value1"));
        Assert.assertTrue(verify(actualLatestTelemetry, "doubleKey", Double.toString(42.0)));
        Assert.assertTrue(verify(actualLatestTelemetry, "longKey", Long.toString(73)));

        restClient.deleteDevice(device.getId());
    }

    @Test
    public void getAttributes() throws Exception {
        restClient.login("tenant@vizzionnaire.org", "tenant");
        TB_TOKEN = restClient.getToken();

        Device device = createDevice("test");
        String accessToken = restClient.getDeviceCredentialsByDeviceId(device.getId()).get().getCredentialsId();
        assertNotNull(accessToken);

        ResponseEntity deviceSharedAttributes = restClient.getRestTemplate()
                .postForEntity(HTTPS_URL + "/api/plugins/telemetry/" + DEVICE + "/" + device.getId().toString() + "/attributes/" + SHARED_SCOPE, mapper.readTree(createPayload().toString()),
                        ResponseEntity.class,
                        accessToken);

        Assert.assertTrue(deviceSharedAttributes.getStatusCode().is2xxSuccessful());

        ResponseEntity deviceClientsAttributes = restClient.getRestTemplate()
                .postForEntity(HTTPS_URL + "/api/v1/" + accessToken + "/attributes/", mapper.readTree(createPayload().toString()),
                        ResponseEntity.class,
                        accessToken);

        Assert.assertTrue(deviceClientsAttributes.getStatusCode().is2xxSuccessful());

        TimeUnit.SECONDS.sleep(3 * timeoutMultiplier);

        @SuppressWarnings("deprecation")
        Optional<JsonNode> allOptional = restClient.getAttributes(accessToken, null, null);
        assertTrue(allOptional.isPresent());


        JsonNode all = allOptional.get();
        assertEquals(2, all.size());
        assertEquals(mapper.readTree(createPayload().toString()), all.get("shared"));
        assertEquals(mapper.readTree(createPayload().toString()), all.get("client"));

        @SuppressWarnings("deprecation")
        Optional<JsonNode> sharedOptional = restClient.getAttributes(accessToken, null, "stringKey");
        assertTrue(sharedOptional.isPresent());

        JsonNode shared = sharedOptional.get();
        assertEquals(shared.get("shared").get("stringKey"), mapper.readTree(createPayload().get("stringKey").toString()));
        assertFalse(shared.has("client"));

        @SuppressWarnings("deprecation")
        Optional<JsonNode> clientOptional = restClient.getAttributes(accessToken, "longKey,stringKey", null);
        assertTrue(clientOptional.isPresent());

        JsonNode client = clientOptional.get();
        assertFalse(client.has("shared"));
        assertEquals(mapper.readTree(createPayload().get("longKey").toString()), client.get("client").get("longKey"));
        assertEquals(client.get("client").get("stringKey"), mapper.readTree(createPayload().get("stringKey").toString()));

        restClient.deleteDevice(device.getId());
    }
}
