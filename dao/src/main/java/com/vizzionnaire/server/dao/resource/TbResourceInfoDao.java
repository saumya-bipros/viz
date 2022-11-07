package com.vizzionnaire.server.dao.resource;

import com.vizzionnaire.server.common.data.TbResourceInfo;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.Dao;

import java.util.UUID;

public interface TbResourceInfoDao extends Dao<TbResourceInfo> {

    PageData<TbResourceInfo> findAllTenantResourcesByTenantId(UUID tenantId, PageLink pageLink);

    PageData<TbResourceInfo> findTenantResourcesByTenantId(UUID tenantId, PageLink pageLink);

}
