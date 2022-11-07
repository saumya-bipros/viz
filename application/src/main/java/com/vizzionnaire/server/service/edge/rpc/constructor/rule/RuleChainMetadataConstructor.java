package com.vizzionnaire.server.service.edge.rpc.constructor.rule;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.RuleChainMetaData;
import com.vizzionnaire.server.gen.edge.v1.RuleChainMetadataUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;

public interface RuleChainMetadataConstructor {

    RuleChainMetadataUpdateMsg constructRuleChainMetadataUpdatedMsg(TenantId tenantId,
                                                                    UpdateMsgType msgType,
                                                                    RuleChainMetaData ruleChainMetaData);
}
