package com.vizzionnaire.rule.engine.transaction;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.rule.engine.api.EmptyNodeConfiguration;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

@Slf4j
@RuleNode(
        type = ComponentType.ACTION,
        name = "synchronization end",
        configClazz = EmptyNodeConfiguration.class,
        nodeDescription = "This Node is now deprecated. Use \"Checkpoint\" instead.",
        nodeDetails = "",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = ("tbNodeEmptyConfig")
)
@Deprecated
public class TbSynchronizationEndNode implements TbNode {

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        log.warn("Synchronization Start/End nodes are deprecated since TB 2.5. Use queue with submit strategy SEQUENTIAL_BY_ORIGINATOR instead.");
        ctx.tellSuccess(msg);
    }

    @Override
    public void destroy() {
    }
}
