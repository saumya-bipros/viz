package com.vizzionnaire.rule.engine.transform;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.common.util.ListeningExecutor;
import com.vizzionnaire.rule.engine.api.ScriptEngine;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.transform.TbTransformMsgNode;
import com.vizzionnaire.rule.engine.transform.TbTransformMsgNodeConfiguration;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgDataType;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TbTransformMsgNodeTest {

    private TbTransformMsgNode node;

    @Mock
    private TbContext ctx;
    @Mock
    private ListeningExecutor executor;
    @Mock
    private ScriptEngine scriptEngine;

    @Test
    public void metadataCanBeUpdated() throws TbNodeException, ScriptException {
        initWithScript();
        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("temp", "7");
        String rawJson = "{\"passed\": 5}";

        RuleChainId ruleChainId = new RuleChainId(Uuids.timeBased());
        RuleNodeId ruleNodeId = new RuleNodeId(Uuids.timeBased());
        TbMsg msg = TbMsg.newMsg( "USER", null, metaData, TbMsgDataType.JSON,rawJson, ruleChainId, ruleNodeId);
        TbMsg transformedMsg = TbMsg.newMsg( "USER", null, metaData, TbMsgDataType.JSON, "{new}", ruleChainId, ruleNodeId);
        when(scriptEngine.executeUpdateAsync(msg)).thenReturn(Futures.immediateFuture(Collections.singletonList(transformedMsg)));

        node.onMsg(ctx, msg);
        ArgumentCaptor<TbMsg> captor = ArgumentCaptor.forClass(TbMsg.class);
        verify(ctx).tellSuccess(captor.capture());
        TbMsg actualMsg = captor.getValue();
        assertEquals(transformedMsg, actualMsg);
    }

    @Test
    public void exceptionHandledCorrectly() throws TbNodeException, ScriptException {
        initWithScript();
        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("temp", "7");
        String rawJson = "{\"passed\": 5";

        RuleChainId ruleChainId = new RuleChainId(Uuids.timeBased());
        RuleNodeId ruleNodeId = new RuleNodeId(Uuids.timeBased());
        TbMsg msg = TbMsg.newMsg( "USER", null, metaData, TbMsgDataType.JSON, rawJson, ruleChainId, ruleNodeId);
        when(scriptEngine.executeUpdateAsync(msg)).thenReturn(Futures.immediateFailedFuture(new IllegalStateException("error")));

        node.onMsg(ctx, msg);
        verifyError(msg, "error", IllegalStateException.class);
    }

    private void initWithScript() throws TbNodeException {
        TbTransformMsgNodeConfiguration config = new TbTransformMsgNodeConfiguration();
        config.setJsScript("scr");
        ObjectMapper mapper = new ObjectMapper();
        TbNodeConfiguration nodeConfiguration = new TbNodeConfiguration(mapper.valueToTree(config));

        when(ctx.createJsScriptEngine("scr")).thenReturn(scriptEngine);

        node = new TbTransformMsgNode();
        node.init(ctx, nodeConfiguration);
    }

    private void verifyError(TbMsg msg, String message, Class expectedClass) {
        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
        verify(ctx).tellFailure(same(msg), captor.capture());

        Throwable value = captor.getValue();
        assertEquals(expectedClass, value.getClass());
        assertEquals(message, value.getMessage());
    }
}
