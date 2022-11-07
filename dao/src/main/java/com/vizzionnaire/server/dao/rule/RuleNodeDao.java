package com.vizzionnaire.server.dao.rule;

import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleNode;
import com.vizzionnaire.server.dao.Dao;

import java.util.List;

/**
 * Created by igor on 3/12/18.
 */
public interface RuleNodeDao extends Dao<RuleNode> {

    List<RuleNode> findRuleNodesByTenantIdAndType(TenantId tenantId, String type, String search);

    PageData<RuleNode> findAllRuleNodesByType(String type, PageLink pageLink);

    List<RuleNode> findByExternalIds(RuleChainId ruleChainId, List<RuleNodeId> externalIds);

    void deleteByIdIn(List<RuleNodeId> ruleNodeIds);
}
