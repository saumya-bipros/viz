package com.vizzionnaire.server.service.rule;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.DefaultRuleChainCreateRequest;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainMetaData;
import com.vizzionnaire.server.common.data.rule.RuleChainOutputLabelsUsage;
import com.vizzionnaire.server.common.data.rule.RuleChainUpdateResult;
import com.vizzionnaire.server.service.entitiy.SimpleTbEntityService;

import java.util.List;
import java.util.Set;

public interface TbRuleChainService extends SimpleTbEntityService<RuleChain> {

    Set<String> getRuleChainOutputLabels(TenantId tenantId, RuleChainId ruleChainId);

    List<RuleChainOutputLabelsUsage> getOutputLabelUsage(TenantId tenantId, RuleChainId ruleChainId);

    List<RuleChain> updateRelatedRuleChains(TenantId tenantId, RuleChainId ruleChainId, RuleChainUpdateResult result);

    RuleChain saveDefaultByName(TenantId tenantId, DefaultRuleChainCreateRequest request, User user) throws Exception;

    RuleChain setRootRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;

    RuleChainMetaData saveRuleChainMetaData(TenantId tenantId, RuleChain ruleChain, RuleChainMetaData ruleChainMetaData,
                                            boolean updateRelated, User user) throws Exception;

    RuleChain assignRuleChainToEdge(TenantId tenantId, RuleChain ruleChain, Edge edge, User user) throws ThingsboardException;

    RuleChain unassignRuleChainFromEdge(TenantId tenantId, RuleChain ruleChain, Edge edge, User user) throws ThingsboardException;

    RuleChain setEdgeTemplateRootRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;

    RuleChain setAutoAssignToEdgeRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;

    RuleChain unsetAutoAssignToEdgeRuleChain(TenantId tenantId, RuleChain ruleChain, User user) throws ThingsboardException;
}
