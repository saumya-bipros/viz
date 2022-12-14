package com.vizzionnaire.rule.engine.transform;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.common.util.ListeningExecutor;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.transform.TbChangeOriginatorNode;
import com.vizzionnaire.rule.engine.transform.TbChangeOriginatorNodeConfiguration;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgDataType;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;
import com.vizzionnaire.server.dao.asset.AssetService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Callable;

import static com.vizzionnaire.rule.engine.api.TbRelationTypes.FAILURE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TbChangeOriginatorNodeTest {

    private TbChangeOriginatorNode node;

    @Mock
    private TbContext ctx;
    @Mock
    private AssetService assetService;

    private ListeningExecutor dbExecutor;

    @Before
    public void before() {
        dbExecutor = new ListeningExecutor() {
            @Override
            public <T> ListenableFuture<T> executeAsync(Callable<T> task) {
                try {
                    return Futures.immediateFuture(task.call());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
    }

    @Test
    public void originatorCanBeChangedToCustomerId() throws TbNodeException {
        init();
        AssetId assetId = new AssetId(Uuids.timeBased());
        CustomerId customerId = new CustomerId(Uuids.timeBased());
        Asset asset = new Asset();
        asset.setCustomerId(customerId);

        RuleChainId ruleChainId = new RuleChainId(Uuids.timeBased());
        RuleNodeId ruleNodeId = new RuleNodeId(Uuids.timeBased());

        TbMsg msg = TbMsg.newMsg( "ASSET", assetId, new TbMsgMetaData(), TbMsgDataType.JSON, "{}", ruleChainId, ruleNodeId);

        when(ctx.getAssetService()).thenReturn(assetService);
        when(assetService.findAssetByIdAsync(any(),eq( assetId))).thenReturn(Futures.immediateFuture(asset));

        node.onMsg(ctx, msg);

        ArgumentCaptor<TbMsg> msgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EntityId> originatorCaptor = ArgumentCaptor.forClass(EntityId.class);
        ArgumentCaptor<TbMsgMetaData> metadataCaptor = ArgumentCaptor.forClass(TbMsgMetaData.class);
        ArgumentCaptor<String> dataCaptor = ArgumentCaptor.forClass(String.class);
        verify(ctx).transformMsg(msgCaptor.capture(), typeCaptor.capture(), originatorCaptor.capture(), metadataCaptor.capture(), dataCaptor.capture());

        assertEquals(customerId, originatorCaptor.getValue());
    }

    @Test
    public void newChainCanBeStarted() throws TbNodeException {
        init();
        AssetId assetId = new AssetId(Uuids.timeBased());
        CustomerId customerId = new CustomerId(Uuids.timeBased());
        Asset asset = new Asset();
        asset.setCustomerId(customerId);

        RuleChainId ruleChainId = new RuleChainId(Uuids.timeBased());
        RuleNodeId ruleNodeId = new RuleNodeId(Uuids.timeBased());

        TbMsg msg = TbMsg.newMsg( "ASSET", assetId, new TbMsgMetaData(), TbMsgDataType.JSON,"{}", ruleChainId, ruleNodeId);

        when(ctx.getAssetService()).thenReturn(assetService);
        when(assetService.findAssetByIdAsync(any(), eq(assetId))).thenReturn(Futures.immediateFuture(asset));

        node.onMsg(ctx, msg);
        ArgumentCaptor<TbMsg> msgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EntityId> originatorCaptor = ArgumentCaptor.forClass(EntityId.class);
        ArgumentCaptor<TbMsgMetaData> metadataCaptor = ArgumentCaptor.forClass(TbMsgMetaData.class);
        ArgumentCaptor<String> dataCaptor = ArgumentCaptor.forClass(String.class);
        verify(ctx).transformMsg(msgCaptor.capture(), typeCaptor.capture(), originatorCaptor.capture(), metadataCaptor.capture(), dataCaptor.capture());

        assertEquals(customerId, originatorCaptor.getValue());
    }

    @Test
    public void exceptionThrownIfCannotFindNewOriginator() throws TbNodeException {
        init();
        AssetId assetId = new AssetId(Uuids.timeBased());
        CustomerId customerId = new CustomerId(Uuids.timeBased());
        Asset asset = new Asset();
        asset.setCustomerId(customerId);

        RuleChainId ruleChainId = new RuleChainId(Uuids.timeBased());
        RuleNodeId ruleNodeId = new RuleNodeId(Uuids.timeBased());

        TbMsg msg = TbMsg.newMsg( "ASSET", assetId, new TbMsgMetaData(), TbMsgDataType.JSON,"{}", ruleChainId, ruleNodeId);

        when(ctx.getAssetService()).thenReturn(assetService);
        when(assetService.findAssetByIdAsync(any(), eq(assetId))).thenReturn(Futures.immediateFuture(null));

        node.onMsg(ctx, msg);
        verify(ctx).tellNext(same(msg), same(FAILURE));
    }

    public void init() throws TbNodeException {
        TbChangeOriginatorNodeConfiguration config = new TbChangeOriginatorNodeConfiguration();
        config.setOriginatorSource(TbChangeOriginatorNode.CUSTOMER_SOURCE);
        ObjectMapper mapper = new ObjectMapper();
        TbNodeConfiguration nodeConfiguration = new TbNodeConfiguration(mapper.valueToTree(config));

        when(ctx.getDbCallbackExecutor()).thenReturn(dbExecutor);

        node = new TbChangeOriginatorNode();
        node.init(null, nodeConfiguration);
    }
}
