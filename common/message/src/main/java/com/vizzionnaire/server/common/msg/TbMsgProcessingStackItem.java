package com.vizzionnaire.server.common.msg;

import lombok.Data;

import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.msg.gen.MsgProtos;

import java.util.UUID;

@Data
public class TbMsgProcessingStackItem {

    private final RuleChainId ruleChainId;
    private final RuleNodeId ruleNodeId;

    MsgProtos.TbMsgProcessingStackItemProto toProto() {
        return MsgProtos.TbMsgProcessingStackItemProto.newBuilder()
                .setRuleChainIdMSB(ruleChainId.getId().getMostSignificantBits())
                .setRuleChainIdLSB(ruleChainId.getId().getLeastSignificantBits())
                .setRuleNodeIdMSB(ruleNodeId.getId().getMostSignificantBits())
                .setRuleNodeIdLSB(ruleNodeId.getId().getLeastSignificantBits())
                .build();
    }

    static TbMsgProcessingStackItem fromProto(MsgProtos.TbMsgProcessingStackItemProto item){
        return new TbMsgProcessingStackItem(
                new RuleChainId(new UUID(item.getRuleChainIdMSB(), item.getRuleChainIdLSB())),
                new RuleNodeId(new UUID(item.getRuleNodeIdMSB(), item.getRuleNodeIdLSB()))
        );
    }

}
