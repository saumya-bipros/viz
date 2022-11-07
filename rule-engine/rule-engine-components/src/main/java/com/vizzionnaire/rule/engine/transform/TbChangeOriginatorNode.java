package com.vizzionnaire.rule.engine.transform;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.rule.engine.util.EntitiesAlarmOriginatorIdAsyncLoader;
import com.vizzionnaire.rule.engine.util.EntitiesCustomerIdAsyncLoader;
import com.vizzionnaire.rule.engine.util.EntitiesRelatedEntityIdAsyncLoader;
import com.vizzionnaire.rule.engine.util.EntitiesTenantIdAsyncLoader;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RuleNode(
        type = ComponentType.TRANSFORMATION,
        name = "change originator",
        configClazz = TbChangeOriginatorNodeConfiguration.class,
        nodeDescription = "Change Message Originator To Tenant/Customer/Related Entity/Alarm Originator",
        nodeDetails = "Related Entity found using configured relation direction and Relation Type. " +
                "If multiple Related Entities are found, only first Entity is used as new Originator, other entities are discarded.<br/>" +
                "Alarm Originator found only in case original Originator is <code>Alarm</code> entity.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbTransformationNodeChangeOriginatorConfig",
        icon = "find_replace"
)
public class TbChangeOriginatorNode extends TbAbstractTransformNode {

    protected static final String CUSTOMER_SOURCE = "CUSTOMER";
    protected static final String TENANT_SOURCE = "TENANT";
    protected static final String RELATED_SOURCE = "RELATED";
    protected static final String ALARM_ORIGINATOR_SOURCE = "ALARM_ORIGINATOR";

    private TbChangeOriginatorNodeConfiguration config;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbChangeOriginatorNodeConfiguration.class);
        validateConfig(config);
        setConfig(config);
    }

    @Override
    protected ListenableFuture<List<TbMsg>> transform(TbContext ctx, TbMsg msg) {
        ListenableFuture<? extends EntityId> newOriginator = getNewOriginator(ctx, msg.getOriginator());
        return Futures.transform(newOriginator, n -> {
            if (n == null || n.isNullUid()) {
                return null;
            }
            return Collections.singletonList((ctx.transformMsg(msg, msg.getType(), n, msg.getMetaData(), msg.getData())));
        }, ctx.getDbCallbackExecutor());
    }

    private ListenableFuture<? extends EntityId> getNewOriginator(TbContext ctx, EntityId original) {
        switch (config.getOriginatorSource()) {
            case CUSTOMER_SOURCE:
                return EntitiesCustomerIdAsyncLoader.findEntityIdAsync(ctx, original);
            case TENANT_SOURCE:
                return EntitiesTenantIdAsyncLoader.findEntityIdAsync(ctx, original);
            case RELATED_SOURCE:
                return EntitiesRelatedEntityIdAsyncLoader.findEntityAsync(ctx, original, config.getRelationsQuery());
            case ALARM_ORIGINATOR_SOURCE:
                return EntitiesAlarmOriginatorIdAsyncLoader.findEntityIdAsync(ctx, original);
            default:
                return Futures.immediateFailedFuture(new IllegalStateException("Unexpected originator source " + config.getOriginatorSource()));
        }
    }

    private void validateConfig(TbChangeOriginatorNodeConfiguration conf) {
        HashSet<String> knownSources = Sets.newHashSet(CUSTOMER_SOURCE, TENANT_SOURCE, RELATED_SOURCE, ALARM_ORIGINATOR_SOURCE);
        if (!knownSources.contains(conf.getOriginatorSource())) {
            log.error("Unsupported source [{}] for TbChangeOriginatorNode", conf.getOriginatorSource());
            throw new IllegalArgumentException("Unsupported source TbChangeOriginatorNode" + conf.getOriginatorSource());
        }

        if (conf.getOriginatorSource().equals(RELATED_SOURCE)) {
            if (conf.getRelationsQuery() == null) {
                log.error("Related source for TbChangeOriginatorNode should have relations query. Actual [{}]",
                        conf.getRelationsQuery());
                throw new IllegalArgumentException("Wrong config for RElated Source in TbChangeOriginatorNode" + conf.getOriginatorSource());
            }
        }

    }

    @Override
    public void destroy() {

    }
}
