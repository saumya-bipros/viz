package com.vizzionnaire.server.transport.lwm2m.rpc.sql;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.transport.lwm2m.rpc.AbstractRpcLwM2MIntegrationTest;

import org.eclipse.leshan.core.ResponseCode;
import org.junit.Test;

import static com.vizzionnaire.server.transport.lwm2m.Lwm2mTestHelper.RESOURCE_ID_14;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class RpcLwm2mIntegrationWriteAttributesTest extends AbstractRpcLwM2MIntegrationTest {

    /**
     * WriteAttributes {"id":"/3/0/14","attributes":{"pmax":100, "pmin":10}}
     * if not implemented:
     * {"result":"INTERNAL_SERVER_ERROR","error":"not implemented"}
     * if implemented:
     * {"result":"CHANGED"}
     */
    @Test
    public void testWriteAttributesResourceWithParametersById_Result_INTERNAL_SERVER_ERROR() throws Exception {
        String expectedPath = objectInstanceIdVer_3 + "/" + RESOURCE_ID_14;
        String expectedValue = "{\"pmax\":100, \"pmin\":10}";
        String actualResult = sendRPCExecuteWithValueById(expectedPath, expectedValue);
        ObjectNode rpcActualResult = JacksonUtil.fromString(actualResult, ObjectNode.class);
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR.getName(), rpcActualResult.get("result").asText());
        String expected = "not implemented";
        String actual = rpcActualResult.get("error").asText();
        assertTrue(actual.equals(expected));
    }

    private String sendRPCExecuteWithValueById(String path, String value) throws Exception {
        String setRpcRequest = "{\"method\": \"WriteAttributes\", \"params\": {\"id\": \"" + path + "\", \"attributes\": " + value + " }}";
        return doPostAsync("/api/plugins/rpc/twoway/" + deviceId, setRpcRequest, String.class, status().isOk());
    }

}
