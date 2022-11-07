package com.vizzionnaire.server.dao.sql.query;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class QuerySecurityContext {

    @Getter
    private final TenantId tenantId;
    @Getter
    private final CustomerId customerId;
    @Getter
    private final EntityType entityType;
    @Getter
    private final boolean ignorePermissionCheck;

    public QuerySecurityContext(TenantId tenantId, CustomerId customerId, EntityType entityType) {
        this(tenantId, customerId, entityType, false);
    }
}