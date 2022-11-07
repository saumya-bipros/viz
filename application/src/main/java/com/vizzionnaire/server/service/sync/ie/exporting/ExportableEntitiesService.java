package com.vizzionnaire.server.service.sync.ie.exporting;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.HasId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;

public interface ExportableEntitiesService {

    <E extends ExportableEntity<I>, I extends EntityId> E findEntityByTenantIdAndExternalId(TenantId tenantId, I externalId);

    <E extends HasId<I>, I extends EntityId> E findEntityByTenantIdAndId(TenantId tenantId, I id);

    <E extends HasId<I>, I extends EntityId> E findEntityById(I id);

    <E extends ExportableEntity<I>, I extends EntityId> E findEntityByTenantIdAndName(TenantId tenantId, EntityType entityType, String name);

    <E extends ExportableEntity<I>, I extends EntityId> PageData<E> findEntitiesByTenantId(TenantId tenantId, EntityType entityType, PageLink pageLink);

    <I extends EntityId> I getExternalIdByInternal(I internalId);

    <I extends EntityId> void removeById(TenantId tenantId, I id);

}
