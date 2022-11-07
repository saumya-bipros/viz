package com.vizzionnaire.rule.engine.metadata;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.rule.engine.util.EntitiesRelatedDeviceIdAsyncLoader;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RuleNode(type = ComponentType.ENRICHMENT,
        name = "related device attributes",
        configClazz = TbGetDeviceAttrNodeConfiguration.class,
        nodeDescription = "Add Originators Related Device Attributes and Latest Telemetry value into Message Metadata",
        nodeDetails = "If Attributes enrichment configured, <b>CLIENT/SHARED/SERVER</b> attributes are added into Message metadata " +
                "with specific prefix: <i>cs/shared/ss</i>. Latest telemetry value added into metadata without prefix. " +
                "To access those attributes in other nodes this template can be used " +
                "<code>metadata.cs_temperature</code> or <code>metadata.shared_limit</code> ",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbEnrichmentNodeDeviceAttributesConfig")
public class TbGetDeviceAttrNode extends TbAbstractGetAttributesNode<TbGetDeviceAttrNodeConfiguration, DeviceId> {

    @Override
    protected TbGetDeviceAttrNodeConfiguration loadGetAttributesNodeConfig(TbNodeConfiguration configuration) throws TbNodeException {
        return TbNodeUtils.convert(configuration, TbGetDeviceAttrNodeConfiguration.class);
    }

    @Override
    protected ListenableFuture<DeviceId> findEntityIdAsync(TbContext ctx, TbMsg msg) {
        return EntitiesRelatedDeviceIdAsyncLoader.findDeviceAsync(ctx, msg.getOriginator(), config.getDeviceRelationsQuery());
    }

}
