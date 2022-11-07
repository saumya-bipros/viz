package com.vizzionnaire.server.actors.ruleChain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.msg.TbActorStopReason;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbRuleEngineActorMsg;
import com.vizzionnaire.server.common.msg.aware.RuleChainAwareMsg;
import com.vizzionnaire.server.common.msg.queue.RuleEngineException;

@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class TbToRuleChainActorMsg extends TbRuleEngineActorMsg implements RuleChainAwareMsg {

    @Getter
    private final RuleChainId target;

    public TbToRuleChainActorMsg(TbMsg msg, RuleChainId target) {
        super(msg);
        this.target = target;
    }

    @Override
    public RuleChainId getRuleChainId() {
        return target;
    }

    @Override
    public void onTbActorStopped(TbActorStopReason reason) {
        String message = reason == TbActorStopReason.STOPPED ? String.format("Rule chain [%s] stopped", target.getId()) : String.format("Failed to initialize rule chain [%s]!", target.getId());
        msg.getCallback().onFailure(new RuleEngineException(message));
    }
}
