package com.vizzionnaire.server.service.entitiy.entity.relation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.dao.relation.RelationService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultTbEntityRelationService extends AbstractTbEntityService implements TbEntityRelationService {

    private final RelationService relationService;

    @Override
    public void save(TenantId tenantId, CustomerId customerId, EntityRelation relation, User user) throws VizzionnaireException {
        try {
            relationService.saveRelation(tenantId, relation);
            notificationEntityService.notifyRelation(tenantId, customerId,
                    relation, user, ActionType.RELATION_ADD_OR_UPDATE, relation);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, relation.getFrom(), null, customerId,
                    ActionType.RELATION_ADD_OR_UPDATE, user, e, relation);
            notificationEntityService.logEntityAction(tenantId, relation.getTo(), null, customerId,
                    ActionType.RELATION_ADD_OR_UPDATE, user, e, relation);
            throw e;
        }
    }

    @Override
    public void delete(TenantId tenantId, CustomerId customerId, EntityRelation relation, User user) throws VizzionnaireException {
        try {
            boolean found = relationService.deleteRelation(tenantId, relation.getFrom(), relation.getTo(), relation.getType(), relation.getTypeGroup());
            if (!found) {
                throw new VizzionnaireException("Requested item wasn't found!", VizzionnaireErrorCode.ITEM_NOT_FOUND);
            }
            notificationEntityService.notifyRelation(tenantId, customerId,
                    relation, user, ActionType.RELATION_DELETED, relation);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, relation.getFrom(), null, customerId,
                    ActionType.RELATION_DELETED, user, e, relation);
            notificationEntityService.logEntityAction(tenantId, relation.getTo(), null, customerId,
                    ActionType.RELATION_DELETED, user, e, relation);
            throw e;
        }
    }

    @Override
    public void deleteRelations(TenantId tenantId, CustomerId customerId, EntityId entityId, User user) throws VizzionnaireException {
        try {
            relationService.deleteEntityRelations(tenantId, entityId);
            notificationEntityService.logEntityAction(tenantId, entityId, null, customerId, ActionType.RELATIONS_DELETED, user);
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, entityId, null, customerId,
                    ActionType.RELATIONS_DELETED, user, e);
            throw e;
        }
    }
}
