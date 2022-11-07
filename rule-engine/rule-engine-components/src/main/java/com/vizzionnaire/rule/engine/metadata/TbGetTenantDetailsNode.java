package com.vizzionnaire.rule.engine.metadata;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.ContactBased;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RuleNode(type = ComponentType.ENRICHMENT,
        name = "tenant details",
        configClazz = TbGetTenantDetailsNodeConfiguration.class,
        nodeDescription = "Adds fields from Tenant details to the message body or metadata",
        nodeDetails = "If checkbox: <b>Add selected details to the message metadata</b> is selected, existing fields will be added to the message metadata instead of message data.<br><br>" +
                "<b>Note:</b> only Device, Asset, and Entity View type are allowed.<br><br>" +
                "If the originator of the message is not assigned to Tenant, or originator type is not supported - Message will be forwarded to <b>Failure</b> chain, otherwise, <b>Success</b> chain will be used.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbEnrichmentNodeEntityDetailsConfig")
public class TbGetTenantDetailsNode extends TbAbstractGetEntityDetailsNode<TbGetTenantDetailsNodeConfiguration> {

    private static final String TENANT_PREFIX = "tenant_";

    @Override
    protected TbGetTenantDetailsNodeConfiguration loadGetEntityDetailsNodeConfiguration(TbNodeConfiguration configuration) throws TbNodeException {
        return TbNodeUtils.convert(configuration, TbGetTenantDetailsNodeConfiguration.class);
    }

    @Override
    protected ListenableFuture<TbMsg> getDetails(TbContext ctx, TbMsg msg) {
        return getTbMsgListenableFuture(ctx, msg, getDataAsJson(msg), TENANT_PREFIX);
    }

    @Override
    protected ListenableFuture<ContactBased> getContactBasedListenableFuture(TbContext ctx, TbMsg msg) {
        return Futures.transformAsync(ctx.getTenantService().findTenantByIdAsync(ctx.getTenantId(), ctx.getTenantId()), tenant -> {
            if (tenant != null) {
                return Futures.immediateFuture(tenant);
            } else {
                return Futures.immediateFuture(null);
            }
        }, MoreExecutors.directExecutor());
    }
}
