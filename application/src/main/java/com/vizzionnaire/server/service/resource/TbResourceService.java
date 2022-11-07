package com.vizzionnaire.server.service.resource;

import com.vizzionnaire.server.common.data.ResourceType;
import com.vizzionnaire.server.common.data.TbResource;
import com.vizzionnaire.server.common.data.TbResourceInfo;
import com.vizzionnaire.server.common.data.id.TbResourceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.lwm2m.LwM2mObject;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.service.entitiy.SimpleTbEntityService;

import java.util.List;

public interface TbResourceService extends SimpleTbEntityService<TbResource> {

    TbResource getResource(TenantId tenantId, ResourceType resourceType, String resourceKey);

    TbResource findResourceById(TenantId tenantId, TbResourceId resourceId);

    TbResourceInfo findResourceInfoById(TenantId tenantId, TbResourceId resourceId);

    PageData<TbResourceInfo> findAllTenantResourcesByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<TbResourceInfo> findTenantResourcesByTenantId(TenantId tenantId, PageLink pageLink);

    List<LwM2mObject> findLwM2mObject(TenantId tenantId,
                                      String sortOrder,
                                      String sortProperty,
                                      String[] objectIds);

    List<LwM2mObject> findLwM2mObjectPage(TenantId tenantId,
                                          String sortProperty,
                                          String sortOrder,
                                          PageLink pageLink);

    void deleteResourcesByTenantId(TenantId tenantId);

    long sumDataSizeByTenantId(TenantId tenantId);
}
