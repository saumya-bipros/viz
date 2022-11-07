package com.vizzionnaire.server.service.entitiy.edge;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.RuleChain;

public interface TbEdgeService {
    Edge save(Edge edge, RuleChain edgeTemplateRootRuleChain, User user) throws Exception;

    void delete(Edge edge, User user);

    Edge assignEdgeToCustomer(TenantId tenantId, EdgeId edgeId, Customer customer, User user) throws ThingsboardException;

    Edge unassignEdgeFromCustomer(Edge edge, Customer customer, User user) throws ThingsboardException;

    Edge assignEdgeToPublicCustomer(TenantId tenantId, EdgeId edgeId, User user) throws ThingsboardException;

    Edge setEdgeRootRuleChain(Edge edge, RuleChainId ruleChainId, User user) throws Exception;
}
