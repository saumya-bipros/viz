package com.vizzionnaire.server.service.entitiy.tenant.profile;

import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface TbTenantProfileService {
    TenantProfile save(TenantId tenantId, TenantProfile tenantProfile, TenantProfile oldTenantProfile) throws ThingsboardException;

    void delete(TenantId tenantId, TenantProfile tenantProfile) throws ThingsboardException;
}
