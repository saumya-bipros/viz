package com.vizzionnaire.rule.engine.metadata;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.rule.engine.util.EntitiesRelatedEntityIdAsyncLoader;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;

@RuleNode(
        type = ComponentType.ENRICHMENT,
        name="related attributes",
        configClazz = TbGetRelatedAttrNodeConfiguration.class,
        nodeDescription = "Add Originators Related Entity Attributes or Latest Telemetry into Message Metadata",
        nodeDetails = "Related Entity found using configured relation direction and Relation Type. " +
                "If multiple Related Entities are found, only first Entity is used for attributes enrichment, other entities are discarded. " +
                "If Attributes enrichment configured, server scope attributes are added into Message metadata. " +
                "If Latest Telemetry enrichment configured, latest telemetry added into metadata. " +
                "To access those attributes in other nodes this template can be used " +
                "<code>metadata.temperature</code>.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbEnrichmentNodeRelatedAttributesConfig")

public class TbGetRelatedAttributeNode extends TbEntityGetAttrNode<EntityId> {

    private TbGetRelatedAttrNodeConfiguration config;

    @Override
    public void init(TbContext context, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbGetRelatedAttrNodeConfiguration.class);
        setConfig(config);
    }

    @Override
    protected ListenableFuture<EntityId> findEntityAsync(TbContext ctx, EntityId originator) {
        return EntitiesRelatedEntityIdAsyncLoader.findEntityAsync(ctx, originator, config.getRelationsQuery());
    }
}
