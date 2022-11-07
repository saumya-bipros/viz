package com.vizzionnaire.rule.engine.mail;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbEmail;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.mail.TbMsgToEmailNode;
import com.vizzionnaire.rule.engine.mail.TbMsgToEmailNodeConfiguration;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgDataType;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TbMsgToEmailNodeTest {

    private TbMsgToEmailNode emailNode;

    @Mock
    private TbContext ctx;

    private EntityId originator = new DeviceId(Uuids.timeBased());
    private TbMsgMetaData metaData = new TbMsgMetaData();
    private String rawJson = "{\"name\": \"temp\", \"passed\": 5 , \"complex\": {\"val\":12, \"count\":100}}";

    private RuleChainId ruleChainId = new RuleChainId(Uuids.timeBased());
    private RuleNodeId ruleNodeId = new RuleNodeId(Uuids.timeBased());

    @Test
    public void msgCanBeConverted() throws IOException {
        initWithScript();
        metaData.putValue("username", "oreo");
        metaData.putValue("userEmail", "user@email.io");
        metaData.putValue("name", "temp");
        metaData.putValue("passed", "5");
        metaData.putValue("count", "100");
        TbMsg msg = TbMsg.newMsg( "USER", originator, metaData, TbMsgDataType.JSON, rawJson, ruleChainId, ruleNodeId);

        emailNode.onMsg(ctx, msg);

        ArgumentCaptor<TbMsg> msgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EntityId> originatorCaptor = ArgumentCaptor.forClass(EntityId.class);
        ArgumentCaptor<TbMsgMetaData> metadataCaptor = ArgumentCaptor.forClass(TbMsgMetaData.class);
        ArgumentCaptor<String> dataCaptor = ArgumentCaptor.forClass(String.class);
        verify(ctx).transformMsg(msgCaptor.capture(), typeCaptor.capture(), originatorCaptor.capture(), metadataCaptor.capture(), dataCaptor.capture());


        assertEquals("SEND_EMAIL", typeCaptor.getValue());
        assertEquals(originator, originatorCaptor.getValue());
        assertEquals("oreo", metadataCaptor.getValue().getValue("username"));
        assertNotSame(metaData, metadataCaptor.getValue());

        TbEmail actual = new ObjectMapper().readValue(dataCaptor.getValue().getBytes(), TbEmail.class);

        TbEmail expected = TbEmail.builder()
                .from("test@mail.org")
                .to("user@email.io")
                .subject("Hi oreo there")
                .body("temp is to high. Current 5 and 100")
                .build();
        assertEquals(expected, actual);
    }

    private void initWithScript() {
        try {
            TbMsgToEmailNodeConfiguration config = new TbMsgToEmailNodeConfiguration();
            config.setFromTemplate("test@mail.org");
            config.setToTemplate("${userEmail}");
            config.setSubjectTemplate("Hi ${username} there");
            config.setBodyTemplate("${name} is to high. Current ${passed} and ${count}");
            config.setMailBodyType("false");
            ObjectMapper mapper = new ObjectMapper();
            TbNodeConfiguration nodeConfiguration = new TbNodeConfiguration(mapper.valueToTree(config));

            emailNode = new TbMsgToEmailNode();
            emailNode.init(ctx, nodeConfiguration);
        } catch (TbNodeException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
