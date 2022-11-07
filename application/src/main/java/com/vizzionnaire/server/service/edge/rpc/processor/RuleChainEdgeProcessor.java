package com.vizzionnaire.server.service.edge.rpc.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainMetaData;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.gen.edge.v1.EdgeVersion;
import com.vizzionnaire.server.gen.edge.v1.RuleChainMetadataUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.RuleChainUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@Slf4j
@TbCoreComponent
public class RuleChainEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg processRuleChainToEdge(Edge edge, EdgeEvent edgeEvent, UpdateMsgType msgType, EdgeEventActionType action) {
        RuleChainId ruleChainId = new RuleChainId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (action) {
            case ADDED:
            case UPDATED:
            case ASSIGNED_TO_EDGE:
                RuleChain ruleChain = ruleChainService.findRuleChainById(edgeEvent.getTenantId(), ruleChainId);
                if (ruleChain != null) {
                    RuleChainUpdateMsg ruleChainUpdateMsg =
                            ruleChainMsgConstructor.constructRuleChainUpdatedMsg(edge.getRootRuleChainId(), msgType, ruleChain);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addRuleChainUpdateMsg(ruleChainUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
            case UNASSIGNED_FROM_EDGE:
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addRuleChainUpdateMsg(ruleChainMsgConstructor.constructRuleChainDeleteMsg(ruleChainId))
                        .build();
                break;
        }
        return downlinkMsg;
    }

    public DownlinkMsg processRuleChainMetadataToEdge(EdgeEvent edgeEvent, UpdateMsgType msgType, EdgeVersion edgeVersion) {
        RuleChainId ruleChainId = new RuleChainId(edgeEvent.getEntityId());
        RuleChain ruleChain = ruleChainService.findRuleChainById(edgeEvent.getTenantId(), ruleChainId);
        DownlinkMsg downlinkMsg = null;
        if (ruleChain != null) {
            RuleChainMetaData ruleChainMetaData = ruleChainService.loadRuleChainMetaData(edgeEvent.getTenantId(), ruleChainId);
            RuleChainMetadataUpdateMsg ruleChainMetadataUpdateMsg =
                    ruleChainMsgConstructor.constructRuleChainMetadataUpdatedMsg(edgeEvent.getTenantId(), msgType, ruleChainMetaData, edgeVersion);
            if (ruleChainMetadataUpdateMsg != null) {
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addRuleChainMetadataUpdateMsg(ruleChainMetadataUpdateMsg)
                        .build();
            }
        }
        return downlinkMsg;
    }
}
