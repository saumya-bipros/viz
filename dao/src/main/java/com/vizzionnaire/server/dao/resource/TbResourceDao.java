package com.vizzionnaire.server.dao.resource;

import com.vizzionnaire.server.common.data.ResourceType;
import com.vizzionnaire.server.common.data.TbResource;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.Dao;
import com.vizzionnaire.server.dao.TenantEntityWithDataDao;

import java.util.List;

public interface TbResourceDao extends Dao<TbResource>, TenantEntityWithDataDao {

    TbResource getResource(TenantId tenantId, ResourceType resourceType, String resourceId);

    PageData<TbResource> findAllByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<TbResource> findResourcesByTenantIdAndResourceType(TenantId tenantId,
                                                                ResourceType resourceType,
                                                                PageLink pageLink);

    List<TbResource> findResourcesByTenantIdAndResourceType(TenantId tenantId,
                                                            ResourceType resourceType,
                                                            String[] objectIds,
                                                            String searchText);
}
