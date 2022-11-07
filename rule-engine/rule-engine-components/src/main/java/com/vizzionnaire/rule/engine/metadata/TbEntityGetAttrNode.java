package com.vizzionnaire.rule.engine.metadata;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.kv.AttributeKvEntry;
import com.vizzionnaire.server.common.data.kv.KvEntry;
import com.vizzionnaire.server.common.data.kv.TsKvEntry;
import com.vizzionnaire.server.common.msg.TbMsg;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vizzionnaire.common.util.DonAsynchron.withCallback;
import static com.vizzionnaire.rule.engine.api.TbRelationTypes.FAILURE;
import static com.vizzionnaire.server.common.data.DataConstants.SERVER_SCOPE;

@Slf4j
public abstract class TbEntityGetAttrNode<T extends EntityId> implements TbNode {

    private TbGetEntityAttrNodeConfiguration config;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbGetEntityAttrNodeConfiguration.class);
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        try {
            withCallback(findEntityAsync(ctx, msg.getOriginator()),
                    entityId -> safeGetAttributes(ctx, msg, entityId),
                    t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
        } catch (Throwable th) {
            ctx.tellFailure(msg, th);
        }
    }

    private void safeGetAttributes(TbContext ctx, TbMsg msg, T entityId) {
        if (entityId == null || entityId.isNullUid()) {
            ctx.tellNext(msg, FAILURE);
            return;
        }

        Map<String, String> mappingsMap = new HashMap<>();
        config.getAttrMapping().forEach((key, value) -> {
            String processPatternKey = TbNodeUtils.processPattern(key, msg);
            String processPatternValue = TbNodeUtils.processPattern(value, msg);
            mappingsMap.put(processPatternKey, processPatternValue);
        });

        List<String> keys = List.copyOf(mappingsMap.keySet());
        withCallback(config.isTelemetry() ? getLatestTelemetry(ctx, entityId, keys) : getAttributesAsync(ctx, entityId, keys),
                attributes -> putAttributesAndTell(ctx, msg, attributes, mappingsMap),
                t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
    }

    private ListenableFuture<List<KvEntry>> getAttributesAsync(TbContext ctx, EntityId entityId, List<String> attrKeys) {
        ListenableFuture<List<AttributeKvEntry>> latest = ctx.getAttributesService().find(ctx.getTenantId(), entityId, SERVER_SCOPE, attrKeys);
        return Futures.transform(latest, l ->
                l.stream().map(i -> (KvEntry) i).collect(Collectors.toList()), MoreExecutors.directExecutor());
    }

    private ListenableFuture<List<KvEntry>> getLatestTelemetry(TbContext ctx, EntityId entityId, List<String> timeseriesKeys) {
        ListenableFuture<List<TsKvEntry>> latest = ctx.getTimeseriesService().findLatest(ctx.getTenantId(), entityId, timeseriesKeys);
        return Futures.transform(latest, l ->
                l.stream().map(i -> (KvEntry) i).collect(Collectors.toList()), MoreExecutors.directExecutor());
    }


    private void putAttributesAndTell(TbContext ctx, TbMsg msg, List<? extends KvEntry> attributes, Map<String, String> map) {
        attributes.forEach(r -> {
            String attrName = map.get(r.getKey());
            msg.getMetaData().putValue(attrName, r.getValueAsString());
        });
        ctx.tellSuccess(msg);
    }

    @Override
    public void destroy() {

    }

    protected abstract ListenableFuture<T> findEntityAsync(TbContext ctx, EntityId originator);

    public void setConfig(TbGetEntityAttrNodeConfiguration config) {
        this.config = config;
    }

}
