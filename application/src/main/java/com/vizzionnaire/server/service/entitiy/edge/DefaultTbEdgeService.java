package com.vizzionnaire.server.service.entitiy.edge;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.edge.EdgeNotificationService;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;

@AllArgsConstructor
@TbCoreComponent
@Service
@Slf4j
public class DefaultTbEdgeService extends AbstractTbEntityService implements TbEdgeService {

    private final EdgeNotificationService edgeNotificationService;
    private final RuleChainService ruleChainService;

    @Override
    public Edge save(Edge edge, RuleChain edgeTemplateRootRuleChain, User user) throws Exception {
        ActionType actionType = edge.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = edge.getTenantId();
        try {
            Edge savedEdge = checkNotNull(edgeService.saveEdge(edge));
            EdgeId edgeId = savedEdge.getId();

            if (actionType == ActionType.ADDED) {
                ruleChainService.assignRuleChainToEdge(tenantId, edgeTemplateRootRuleChain.getId(), edgeId);
                edgeNotificationService.setEdgeRootRuleChain(tenantId, savedEdge, edgeTemplateRootRuleChain.getId());
                edgeService.assignDefaultRuleChainsToEdge(tenantId, edgeId);
            }

            notificationEntityService.notifyEdge(tenantId, edgeId, savedEdge.getCustomerId(), savedEdge, actionType, user);

            return savedEdge;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.EDGE), edge, actionType, user, e);
            throw e;
        }
    }

    @Override
    public void delete(Edge edge, User user) {
        EdgeId edgeId = edge.getId();
        TenantId tenantId = edge.getTenantId();
        try {
            edgeService.deleteEdge(tenantId, edgeId);
            notificationEntityService.notifyEdge(tenantId, edgeId, edge.getCustomerId(), edge, ActionType.DELETED, user, edgeId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.EDGE), ActionType.DELETED,
                    user, e, edgeId.toString());
            throw e;
        }
    }

    @Override
    public Edge assignEdgeToCustomer(TenantId tenantId, EdgeId edgeId, Customer customer, User user) throws VizzionnaireException {
        CustomerId customerId = customer.getId();
        try {
            Edge savedEdge = checkNotNull(edgeService.assignEdgeToCustomer(tenantId, edgeId, customerId));
            notificationEntityService.notifyEdge(tenantId, edgeId, customerId, savedEdge, ActionType.ASSIGNED_TO_CUSTOMER, user,
                    edgeId.toString(), customerId.toString(), customer.getName());

            return savedEdge;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.EDGE),
                    ActionType.ASSIGNED_TO_CUSTOMER, user, e, edgeId.toString(), customerId.toString());
            throw e;
        }
    }

    @Override
    public Edge unassignEdgeFromCustomer(Edge edge, Customer customer, User user) throws VizzionnaireException {
        TenantId tenantId = edge.getTenantId();
        EdgeId edgeId = edge.getId();
        CustomerId customerId = customer.getId();
        try {
            Edge savedEdge = checkNotNull(edgeService.unassignEdgeFromCustomer(tenantId, edgeId));

            notificationEntityService.notifyEdge(tenantId, edgeId, customerId, savedEdge, ActionType.UNASSIGNED_FROM_CUSTOMER, user,
                    edgeId.toString(), customerId.toString(), customer.getName());
            return savedEdge;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.EDGE),
                    ActionType.UNASSIGNED_FROM_CUSTOMER, user, e, edgeId.toString());
            throw e;
        }
    }

    @Override
    public Edge assignEdgeToPublicCustomer(TenantId tenantId, EdgeId edgeId, User user) throws VizzionnaireException {
        Customer publicCustomer = customerService.findOrCreatePublicCustomer(tenantId);
        CustomerId customerId = publicCustomer.getId();
        try {
            Edge savedEdge = checkNotNull(edgeService.assignEdgeToCustomer(tenantId, edgeId, customerId));

            notificationEntityService.notifyEdge(tenantId, edgeId, customerId, savedEdge, ActionType.ASSIGNED_TO_CUSTOMER, user,
                    edgeId.toString(), customerId.toString(), publicCustomer.getName());

            return savedEdge;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.EDGE),
                    ActionType.ASSIGNED_TO_CUSTOMER, user, e, edgeId.toString());
            throw e;
        }
    }

    @Override
    public Edge setEdgeRootRuleChain(Edge edge, RuleChainId ruleChainId, User user) throws Exception {
        TenantId tenantId = edge.getTenantId();
        EdgeId edgeId = edge.getId();
        try {
            Edge updatedEdge = edgeNotificationService.setEdgeRootRuleChain(tenantId, edge, ruleChainId);
            notificationEntityService.notifyEdge(tenantId, edgeId, null, updatedEdge, ActionType.UPDATED, user);
            return updatedEdge;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.EDGE),
                    ActionType.UPDATED, user, e, edgeId.toString());
            throw e;
        }
    }
}
