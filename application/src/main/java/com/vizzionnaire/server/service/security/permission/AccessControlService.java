package com.vizzionnaire.server.service.security.permission;

import com.vizzionnaire.server.common.data.HasTenantId;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.service.security.model.SecurityUser;

public interface AccessControlService {

    void checkPermission(SecurityUser user, Resource resource, Operation operation) throws VizzionnaireException;

    <I extends EntityId, T extends HasTenantId> void checkPermission(SecurityUser user, Resource resource, Operation operation, I entityId, T entity) throws VizzionnaireException;

}
