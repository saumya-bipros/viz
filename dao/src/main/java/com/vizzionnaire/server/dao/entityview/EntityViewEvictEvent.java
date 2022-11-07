package com.vizzionnaire.server.dao.entityview;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class EntityViewEvictEvent {

    private final TenantId tenantId;
    private final EntityViewId id;
    private final EntityId newEntityId;
    private final EntityId oldEntityId;
    private final String newName;
    private final String oldName;

}
