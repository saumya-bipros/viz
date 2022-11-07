package com.vizzionnaire.server.dao.rule;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleNodeState;

public interface RuleNodeStateService {

    PageData<RuleNodeState> findByRuleNodeId(TenantId tenantId, RuleNodeId ruleNodeId, PageLink pageLink);

    RuleNodeState findByRuleNodeIdAndEntityId(TenantId tenantId, RuleNodeId ruleNodeId, EntityId entityId);

    RuleNodeState save(TenantId tenantId, RuleNodeState ruleNodeState);

    void removeByRuleNodeId(TenantId tenantId, RuleNodeId selfId);

    void removeByRuleNodeIdAndEntityId(TenantId tenantId, RuleNodeId selfId, EntityId entityId);
}
