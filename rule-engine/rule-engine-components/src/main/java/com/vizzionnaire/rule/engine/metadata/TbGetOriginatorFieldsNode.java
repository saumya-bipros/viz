package com.vizzionnaire.rule.engine.metadata;

import static com.vizzionnaire.common.util.DonAsynchron.withCallback;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.rule.engine.util.EntitiesFieldsAsyncLoader;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by ashvayka on 19.01.18.
 */
@Slf4j
@RuleNode(type = ComponentType.ENRICHMENT,
        name = "originator fields",
        configClazz = TbGetOriginatorFieldsConfiguration.class,
        nodeDescription = "Add Message Originator fields values into Message Metadata",
        nodeDetails = "Will fetch fields values specified in mapping. If specified field is not part of originator fields it will be ignored.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbEnrichmentNodeOriginatorFieldsConfig")
public class TbGetOriginatorFieldsNode implements TbNode {

    private TbGetOriginatorFieldsConfiguration config;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        config = TbNodeUtils.convert(configuration, TbGetOriginatorFieldsConfiguration.class);
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        try {
            withCallback(putEntityFields(ctx, msg.getOriginator(), msg),
                    i -> ctx.tellSuccess(msg), t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
        } catch (Throwable th) {
            ctx.tellFailure(msg, th);
        }
    }

    private ListenableFuture<Void> putEntityFields(TbContext ctx, EntityId entityId, TbMsg msg) {
        if (config.getFieldsMapping().isEmpty()) {
            return Futures.immediateFuture(null);
        } else {
            return Futures.transform(EntitiesFieldsAsyncLoader.findAsync(ctx, entityId),
                    data -> {
                        config.getFieldsMapping().forEach((field, metaKey) -> {
                            String val = data.getFieldValue(field);
                            if (val != null) {
                                msg.getMetaData().putValue(metaKey, val);
                            }
                        });
                        return null;
                    }, MoreExecutors.directExecutor()
            );
        }
    }

    @Override
    public void destroy() {

    }
}
