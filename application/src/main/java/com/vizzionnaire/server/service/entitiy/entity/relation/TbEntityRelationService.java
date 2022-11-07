package com.vizzionnaire.server.service.entitiy.entity.relation;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.relation.EntityRelation;

public interface TbEntityRelationService {

    void save(TenantId tenantId, CustomerId customerId, EntityRelation entity, User user) throws ThingsboardException;

    void delete(TenantId tenantId, CustomerId customerId, EntityRelation entity, User user) throws ThingsboardException;

    void deleteRelations(TenantId tenantId, CustomerId customerId, EntityId entityId, User user) throws ThingsboardException;

}
