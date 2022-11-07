package com.vizzionnaire.rule.engine.metadata;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.util.EntitiesCustomerIdAsyncLoader;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;

@RuleNode(
        type = ComponentType.ENRICHMENT,
        name="customer attributes",
        configClazz = TbGetEntityAttrNodeConfiguration.class,
        nodeDescription = "Add Originators Customer Attributes or Latest Telemetry into Message Metadata",
        nodeDetails = "If Attributes enrichment configured, server scope attributes are added into Message metadata. " +
                "If Latest Telemetry enrichment configured, latest telemetry added into metadata. " +
                "To access those attributes in other nodes this template can be used " +
                "<code>metadata.temperature</code>.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbEnrichmentNodeCustomerAttributesConfig")
public class TbGetCustomerAttributeNode extends TbEntityGetAttrNode<CustomerId> {

    @Override
    protected ListenableFuture<CustomerId> findEntityAsync(TbContext ctx, EntityId originator) {
        return EntitiesCustomerIdAsyncLoader.findEntityIdAsync(ctx, originator);
    }

}
