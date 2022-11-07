package com.vizzionnaire.rule.engine.flow;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.TbRelationTypes;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

import java.util.UUID;

@Slf4j
@RuleNode(
        type = ComponentType.FLOW,
        name = "rule chain",
        configClazz = TbRuleChainInputNodeConfiguration.class,
        nodeDescription = "transfers the message to another rule chain",
        nodeDetails = "Allows to nest the rule chain similar to single rule node. " +
                "The incoming message is forwarded to the input node of the specified target rule chain. " +
                "The target rule chain may produce multiple labeled outputs. " +
                "You may use the outputs to forward the results of processing to other rule nodes.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbFlowNodeRuleChainInputConfig",
        relationTypes = {},
        ruleChainNode = true,
        customRelations = true
)
public class TbRuleChainInputNode implements TbNode {

    private TbRuleChainInputNodeConfiguration config;
    private RuleChainId ruleChainId;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbRuleChainInputNodeConfiguration.class);
        this.ruleChainId = new RuleChainId(UUID.fromString(config.getRuleChainId()));
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        ctx.input(msg, ruleChainId);
    }

    @Override
    public void destroy() {
    }
}
