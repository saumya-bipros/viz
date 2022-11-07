package com.vizzionnaire.rule.engine.action;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.common.util.ListeningExecutor;
import com.vizzionnaire.rule.engine.action.TbCreateRelationNode;
import com.vizzionnaire.rule.engine.action.TbCreateRelationNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.TbRelationTypes;
import com.vizzionnaire.server.common.data.DataConstants;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;
import com.vizzionnaire.server.common.data.relation.RelationTypeGroup;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgDataType;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.relation.RelationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TbCreateRelationNodeTest {

    private static final String RELATION_TYPE_CONTAINS = "Contains";

    private TbCreateRelationNode node;

    @Mock
    private TbContext ctx;
    @Mock
    private AssetService assetService;
    @Mock
    private RelationService relationService;

    private TbMsg msg;

    private RuleChainId ruleChainId = new RuleChainId(Uuids.timeBased());
    private RuleNodeId ruleNodeId = new RuleNodeId(Uuids.timeBased());

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
    public void testCreateNewRelation() throws TbNodeException {
        init(createRelationNodeConfig());

        DeviceId deviceId = new DeviceId(Uuids.timeBased());

        AssetId assetId = new AssetId(Uuids.timeBased());
        Asset asset = new Asset();
        asset.setId(assetId);

        when(assetService.findAssetByTenantIdAndName(any(), eq("AssetName"))).thenReturn(asset);
        when(assetService.findAssetByIdAsync(any(), eq(assetId))).thenReturn(Futures.immediateFuture(asset));

        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("name", "AssetName");
        metaData.putValue("type", "AssetType");
        msg = TbMsg.newMsg(DataConstants.ENTITY_CREATED, deviceId, metaData, TbMsgDataType.JSON, "{}", ruleChainId, ruleNodeId);

        when(ctx.getRelationService().checkRelationAsync(any(), eq(assetId), eq(deviceId), eq(RELATION_TYPE_CONTAINS), eq(RelationTypeGroup.COMMON)))
                .thenReturn(Futures.immediateFuture(false));
        when(ctx.getRelationService().saveRelationAsync(any(), eq(new EntityRelation(assetId, deviceId, RELATION_TYPE_CONTAINS, RelationTypeGroup.COMMON))))
                .thenReturn(Futures.immediateFuture(true));

        node.onMsg(ctx, msg);
        verify(ctx).tellNext(msg, TbRelationTypes.SUCCESS);
    }

    @Test
    public void testDeleteCurrentRelationsCreateNewRelation() throws TbNodeException {
        init(createRelationNodeConfigWithRemoveCurrentRelations());

        DeviceId deviceId = new DeviceId(Uuids.timeBased());

        AssetId assetId = new AssetId(Uuids.timeBased());
        Asset asset = new Asset();
        asset.setId(assetId);

        when(assetService.findAssetByTenantIdAndName(any(), eq("AssetName"))).thenReturn(asset);
        when(assetService.findAssetByIdAsync(any(), eq(assetId))).thenReturn(Futures.immediateFuture(asset));

        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("name", "AssetName");
        metaData.putValue("type", "AssetType");
        msg = TbMsg.newMsg(DataConstants.ENTITY_CREATED, deviceId, metaData, TbMsgDataType.JSON, "{}", ruleChainId, ruleNodeId);

        EntityRelation relation = new EntityRelation();
        when(ctx.getRelationService().findByToAndTypeAsync(any(), eq(msg.getOriginator()), eq(RELATION_TYPE_CONTAINS), eq(RelationTypeGroup.COMMON)))
                .thenReturn(Futures.immediateFuture(Collections.singletonList(relation)));
        when(ctx.getRelationService().deleteRelationAsync(any(), eq(relation))).thenReturn(Futures.immediateFuture(true));
        when(ctx.getRelationService().checkRelationAsync(any(), eq(assetId), eq(deviceId), eq(RELATION_TYPE_CONTAINS), eq(RelationTypeGroup.COMMON)))
                .thenReturn(Futures.immediateFuture(false));
        when(ctx.getRelationService().saveRelationAsync(any(), eq(new EntityRelation(assetId, deviceId, RELATION_TYPE_CONTAINS, RelationTypeGroup.COMMON))))
                .thenReturn(Futures.immediateFuture(true));

        node.onMsg(ctx, msg);
        verify(ctx).tellNext(msg, TbRelationTypes.SUCCESS);
    }

    @Test
    public void testCreateNewRelationAndChangeOriginator() throws TbNodeException {
        init(createRelationNodeConfigWithChangeOriginator());

        DeviceId deviceId = new DeviceId(Uuids.timeBased());

        AssetId assetId = new AssetId(Uuids.timeBased());
        Asset asset = new Asset();
        asset.setId(assetId);

        when(assetService.findAssetByTenantIdAndName(any(), eq("AssetName"))).thenReturn(asset);
        when(assetService.findAssetByIdAsync(any(), eq(assetId))).thenReturn(Futures.immediateFuture(asset));

        TbMsgMetaData metaData = new TbMsgMetaData();
        metaData.putValue("name", "AssetName");
        metaData.putValue("type", "AssetType");
        msg = TbMsg.newMsg(DataConstants.ENTITY_CREATED, deviceId, metaData, TbMsgDataType.JSON, "{}", ruleChainId, ruleNodeId);

        when(ctx.getRelationService().checkRelationAsync(any(), eq(assetId), eq(deviceId), eq(RELATION_TYPE_CONTAINS), eq(RelationTypeGroup.COMMON)))
                .thenReturn(Futures.immediateFuture(false));
        when(ctx.getRelationService().saveRelationAsync(any(), eq(new EntityRelation(assetId, deviceId, RELATION_TYPE_CONTAINS, RelationTypeGroup.COMMON))))
                .thenReturn(Futures.immediateFuture(true));

        node.onMsg(ctx, msg);
        ArgumentCaptor<TbMsg> msgCaptor = ArgumentCaptor.forClass(TbMsg.class);
        ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<EntityId> originatorCaptor = ArgumentCaptor.forClass(EntityId.class);
        ArgumentCaptor<TbMsgMetaData> metadataCaptor = ArgumentCaptor.forClass(TbMsgMetaData.class);
        ArgumentCaptor<String> dataCaptor = ArgumentCaptor.forClass(String.class);
        verify(ctx).transformMsg(msgCaptor.capture(), typeCaptor.capture(), originatorCaptor.capture(), metadataCaptor.capture(), dataCaptor.capture());

        assertEquals(assetId, originatorCaptor.getValue());
    }

    public void init(TbCreateRelationNodeConfiguration configuration) throws TbNodeException {
        ObjectMapper mapper = new ObjectMapper();
        TbNodeConfiguration nodeConfiguration = new TbNodeConfiguration(mapper.valueToTree(configuration));

        when(ctx.getDbCallbackExecutor()).thenReturn(dbExecutor);
        when(ctx.getRelationService()).thenReturn(relationService);
        when(ctx.getAssetService()).thenReturn(assetService);

        node = new TbCreateRelationNode();
        node.init(ctx, nodeConfiguration);
    }

    private TbCreateRelationNodeConfiguration createRelationNodeConfig() {
        TbCreateRelationNodeConfiguration configuration = new TbCreateRelationNodeConfiguration();
        configuration.setDirection(EntitySearchDirection.FROM.name());
        configuration.setRelationType(RELATION_TYPE_CONTAINS);
        configuration.setEntityCacheExpiration(300);
        configuration.setEntityType("ASSET");
        configuration.setEntityNamePattern("${name}");
        configuration.setEntityTypePattern("${type}");
        configuration.setCreateEntityIfNotExists(false);
        configuration.setChangeOriginatorToRelatedEntity(false);
        configuration.setRemoveCurrentRelations(false);
        return configuration;
    }

    private TbCreateRelationNodeConfiguration createRelationNodeConfigWithRemoveCurrentRelations() {
        TbCreateRelationNodeConfiguration configuration = createRelationNodeConfig();
        configuration.setRemoveCurrentRelations(true);
        return configuration;
    }

    private TbCreateRelationNodeConfiguration createRelationNodeConfigWithChangeOriginator() {
        TbCreateRelationNodeConfiguration configuration = createRelationNodeConfig();
        configuration.setChangeOriginatorToRelatedEntity(true);
        return configuration;
    }
}
