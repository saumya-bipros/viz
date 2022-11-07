package com.vizzionnaire.server.dao.tenant;

import com.vizzionnaire.server.common.data.EntityInfo;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.Dao;

import java.util.UUID;

public interface TenantProfileDao extends Dao<TenantProfile> {

    EntityInfo findTenantProfileInfoById(TenantId tenantId, UUID tenantProfileId);

    TenantProfile save(TenantId tenantId, TenantProfile tenantProfile);

    PageData<TenantProfile> findTenantProfiles(TenantId tenantId, PageLink pageLink);

    PageData<EntityInfo> findTenantProfileInfos(TenantId tenantId, PageLink pageLink);

    TenantProfile findDefaultTenantProfile(TenantId tenantId);

    EntityInfo findDefaultTenantProfileInfo(TenantId tenantId);

}
