package com.vizzionnaire.rule.engine.flow;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.rule.engine.api.EmptyNodeConfiguration;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.TbRelationTypes;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

@Slf4j
@RuleNode(
        type = ComponentType.FLOW,
        name = "output",
        configClazz = EmptyNodeConfiguration.class,
        nodeDescription = "transfers the message to the caller rule chain",
        nodeDetails = "Produces output of the rule chain processing. " +
                "The output is forwarded to the caller rule chain, as an output of the corresponding \"input\" rule node. " +
                "The output rule node name corresponds to the relation type of the output message, and it is used to forward messages to other rule nodes in the caller rule chain. ",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbFlowNodeRuleChainOutputConfig",
        outEnabled = false
)
public class TbRuleChainOutputNode implements TbNode {

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        ctx.output(msg, ctx.getSelf().getName());
    }

    @Override
    public void destroy() {
    }
}
