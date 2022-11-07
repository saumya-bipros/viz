package com.vizzionnaire.server.dao;

import java.util.UUID;

import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;

public interface ExportableEntityDao<I extends EntityId, T extends ExportableEntity<?>> extends Dao<T> {

    T findByTenantIdAndExternalId(UUID tenantId, UUID externalId);

    default T findByTenantIdAndName(UUID tenantId, String name) { throw new UnsupportedOperationException(); }

    PageData<T> findByTenantId(UUID tenantId, PageLink pageLink);

    I getExternalIdByInternal(I internalId);

}
