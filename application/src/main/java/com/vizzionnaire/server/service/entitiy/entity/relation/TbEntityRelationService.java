package com.vizzionnaire.server.service.entitiy.entity.relation;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.relation.EntityRelation;

public interface TbEntityRelationService {

    void save(TenantId tenantId, CustomerId customerId, EntityRelation entity, User user) throws VizzionnaireException;

    void delete(TenantId tenantId, CustomerId customerId, EntityRelation entity, User user) throws VizzionnaireException;

    void deleteRelations(TenantId tenantId, CustomerId customerId, EntityId entityId, User user) throws VizzionnaireException;

}
