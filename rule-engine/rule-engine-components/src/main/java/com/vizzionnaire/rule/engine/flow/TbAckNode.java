package com.vizzionnaire.rule.engine.flow;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.rule.engine.api.EmptyNodeConfiguration;
import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

@Slf4j
@RuleNode(
        type = ComponentType.FLOW,
        name = "acknowledge",
        configClazz = EmptyNodeConfiguration.class,
        nodeDescription = "Acknowledges the incoming message",
        nodeDetails = "After acknowledgement, the message is pushed to related rule nodes. Useful if you don't care what happens to this message next.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbNodeEmptyConfig"
)
public class TbAckNode implements TbNode {

    EmptyNodeConfiguration config;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, EmptyNodeConfiguration.class);
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        ctx.ack(msg);
        ctx.tellSuccess(msg);
    }

    @Override
    public void destroy() {
    }
}
