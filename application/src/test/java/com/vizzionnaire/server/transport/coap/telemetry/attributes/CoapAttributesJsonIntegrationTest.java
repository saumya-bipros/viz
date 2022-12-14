package com.vizzionnaire.server.transport.coap.telemetry.attributes;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.vizzionnaire.server.common.data.CoapDeviceType;
import com.vizzionnaire.server.common.data.TransportPayloadType;
import com.vizzionnaire.server.dao.service.DaoSqlTest;
import com.vizzionnaire.server.transport.coap.CoapTestConfigProperties;

@Slf4j
@DaoSqlTest
public class CoapAttributesJsonIntegrationTest extends CoapAttributesIntegrationTest {

    @Before
    public void beforeTest() throws Exception {
        CoapTestConfigProperties configProperties = CoapTestConfigProperties.builder()
                .deviceName("Test Post Attributes device Json")
                .coapDeviceType(CoapDeviceType.DEFAULT)
                .transportPayloadType(TransportPayloadType.JSON)
                .build();
        processBeforeTest(configProperties);
    }

    @After
    public void afterTest() throws Exception {
        processAfterTest();
    }

    @Test
    public void testPushAttributes() throws Exception {
        super.testPushAttributes();
    }
}
