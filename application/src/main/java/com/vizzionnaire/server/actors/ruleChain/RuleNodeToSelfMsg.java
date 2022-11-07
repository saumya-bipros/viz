package com.vizzionnaire.server.actors.ruleChain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorStopReason;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbRuleEngineActorMsg;
import com.vizzionnaire.server.common.msg.queue.RuleNodeException;

/**
 * Created by ashvayka on 19.03.18.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
final class RuleNodeToSelfMsg extends TbToRuleNodeActorMsg {

    public RuleNodeToSelfMsg(TbContext ctx, TbMsg tbMsg) {
        super(ctx, tbMsg);
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.RULE_TO_SELF_MSG;
    }

}
