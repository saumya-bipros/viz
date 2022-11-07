package com.vizzionnaire.server.service.security.permission;

import com.vizzionnaire.server.common.data.HasTenantId;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.service.security.model.SecurityUser;

public interface AccessControlService {

    void checkPermission(SecurityUser user, Resource resource, Operation operation) throws ThingsboardException;

    <I extends EntityId, T extends HasTenantId> void checkPermission(SecurityUser user, Resource resource, Operation operation, I entityId, T entity) throws ThingsboardException;

}
