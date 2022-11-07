package com.vizzionnaire.server.actors.ruleChain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbMsg;

/**
 * Created by ashvayka on 19.03.18.
 */
@EqualsAndHashCode(callSuper = true)
@ToString
public final class RuleChainInputMsg extends TbToRuleChainActorMsg {

    public RuleChainInputMsg(RuleChainId target, TbMsg tbMsg) {
        super(tbMsg, target);
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.RULE_CHAIN_INPUT_MSG;
    }
}
