package com.vizzionnaire.rule.engine.metadata;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.util.EntitiesTenantIdAsyncLoader;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RuleNode(
        type = ComponentType.ENRICHMENT,
        name="tenant attributes",
        configClazz = TbGetEntityAttrNodeConfiguration.class,
        nodeDescription = "Add Originators Tenant Attributes or Latest Telemetry into Message Metadata",
        nodeDetails = "If Attributes enrichment configured, server scope attributes are added into Message metadata. " +
                "If Latest Telemetry enrichment configured, latest telemetry added into metadata. " +
                "To access those attributes in other nodes this template can be used " +
                "<code>metadata.temperature</code>.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbEnrichmentNodeTenantAttributesConfig")
public class TbGetTenantAttributeNode extends TbEntityGetAttrNode<TenantId> {

    @Override
    protected ListenableFuture<TenantId> findEntityAsync(TbContext ctx, EntityId originator) {
        return EntitiesTenantIdAsyncLoader.findEntityIdAsync(ctx, originator);
    }

}
