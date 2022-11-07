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
public final class RuleChainOutputMsg extends TbToRuleChainActorMsg {

    @Getter
    private final RuleNodeId targetRuleNodeId;

    @Getter
    private final String relationType;

    public RuleChainOutputMsg(RuleChainId target, RuleNodeId targetRuleNodeId, String relationType, TbMsg tbMsg) {
        super(tbMsg, target);
        this.targetRuleNodeId = targetRuleNodeId;
        this.relationType = relationType;
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.RULE_CHAIN_OUTPUT_MSG;
    }
}
