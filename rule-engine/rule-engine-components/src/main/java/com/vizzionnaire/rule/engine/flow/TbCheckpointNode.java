package com.vizzionnaire.rule.engine.flow;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.rule.engine.api.RuleNode;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.TbRelationTypes;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.id.QueueId;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.msg.TbMsg;

import java.util.UUID;

@Slf4j
@RuleNode(
        type = ComponentType.FLOW,
        name = "checkpoint",
        configClazz = TbCheckpointNodeConfiguration.class,
        nodeDescription = "transfers the message to another queue",
        nodeDetails = "After successful transfer incoming message is automatically acknowledged. Queue name is configurable.",
        uiResources = {"static/rulenode/rulenode-core-config.js"},
        configDirective = "tbActionNodeCheckPointConfig"
)
public class TbCheckpointNode implements TbNode {

    private String queueName;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        TbCheckpointNodeConfiguration config = TbNodeUtils.convert(configuration, TbCheckpointNodeConfiguration.class);
        this.queueName = config.getQueueName();
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        ctx.enqueueForTellNext(msg, queueName, TbRelationTypes.SUCCESS, () -> ctx.ack(msg), error -> ctx.tellFailure(msg, error));
    }

    @Override
    public void destroy() {
    }
}
