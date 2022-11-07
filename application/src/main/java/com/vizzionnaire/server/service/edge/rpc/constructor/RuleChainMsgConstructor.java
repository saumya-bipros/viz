package com.vizzionnaire.server.service.edge.rpc.constructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainMetaData;
import com.vizzionnaire.server.gen.edge.v1.EdgeVersion;
import com.vizzionnaire.server.gen.edge.v1.RuleChainMetadataUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.RuleChainUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.edge.rpc.constructor.rule.RuleChainMetadataConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.rule.RuleChainMetadataConstructorFactory;

@Component
@Slf4j
@TbCoreComponent
public class RuleChainMsgConstructor {

    public RuleChainUpdateMsg constructRuleChainUpdatedMsg(RuleChainId edgeRootRuleChainId, UpdateMsgType msgType, RuleChain ruleChain) {
        RuleChainUpdateMsg.Builder builder = RuleChainUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(ruleChain.getId().getId().getMostSignificantBits())
                .setIdLSB(ruleChain.getId().getId().getLeastSignificantBits())
                .setName(ruleChain.getName())
                .setRoot(ruleChain.getId().equals(edgeRootRuleChainId))
                .setDebugMode(ruleChain.isDebugMode())
                .setConfiguration(JacksonUtil.toString(ruleChain.getConfiguration()));
        if (ruleChain.getFirstRuleNodeId() != null) {
            builder.setFirstRuleNodeIdMSB(ruleChain.getFirstRuleNodeId().getId().getMostSignificantBits())
                    .setFirstRuleNodeIdLSB(ruleChain.getFirstRuleNodeId().getId().getLeastSignificantBits());
        }
        return builder.build();
    }

    public RuleChainMetadataUpdateMsg constructRuleChainMetadataUpdatedMsg(TenantId tenantId,
                                                                           UpdateMsgType msgType,
                                                                           RuleChainMetaData ruleChainMetaData,
                                                                           EdgeVersion edgeVersion) {
        RuleChainMetadataConstructor ruleChainMetadataConstructor
                = RuleChainMetadataConstructorFactory.getByEdgeVersion(edgeVersion);
        return ruleChainMetadataConstructor.constructRuleChainMetadataUpdatedMsg(tenantId, msgType, ruleChainMetaData);
    }

    public RuleChainUpdateMsg constructRuleChainDeleteMsg(RuleChainId ruleChainId) {
        return RuleChainUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(ruleChainId.getId().getMostSignificantBits())
                .setIdLSB(ruleChainId.getId().getLeastSignificantBits()).build();
    }
}
