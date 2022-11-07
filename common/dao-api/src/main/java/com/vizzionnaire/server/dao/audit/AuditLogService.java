package com.vizzionnaire.server.dao.audit;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.HasName;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.audit.AuditLog;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.TimePageLink;

import java.util.List;

public interface AuditLogService {

    PageData<AuditLog> findAuditLogsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, List<ActionType> actionTypes, TimePageLink pageLink);

    PageData<AuditLog> findAuditLogsByTenantIdAndUserId(TenantId tenantId, UserId userId, List<ActionType> actionTypes, TimePageLink pageLink);

    PageData<AuditLog> findAuditLogsByTenantIdAndEntityId(TenantId tenantId, EntityId entityId, List<ActionType> actionTypes, TimePageLink pageLink);

    PageData<AuditLog> findAuditLogsByTenantId(TenantId tenantId, List<ActionType> actionTypes, TimePageLink pageLink);

    <E extends HasName, I extends EntityId> ListenableFuture<List<Void>> logEntityAction(
            TenantId tenantId,
            CustomerId customerId,
            UserId userId,
            String userName,
            I entityId,
            E entity,
            ActionType actionType,
            Exception e, Object... additionalInfo);
}
