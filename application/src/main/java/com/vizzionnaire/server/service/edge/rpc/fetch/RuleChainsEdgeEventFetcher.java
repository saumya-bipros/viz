package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.dao.rule.RuleChainService;

@Slf4j
@AllArgsConstructor
public class RuleChainsEdgeEventFetcher extends BasePageableEdgeEventFetcher<RuleChain> {

    private final RuleChainService ruleChainService;

    @Override
    PageData<RuleChain> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return ruleChainService.findRuleChainsByTenantIdAndEdgeId(tenantId, edge.getId(), pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, RuleChain ruleChain) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.RULE_CHAIN,
                EdgeEventActionType.ADDED, ruleChain.getId(), null);
    }
}
