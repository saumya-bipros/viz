package com.vizzionnaire.server.service.entitiy.tenant;

import com.vizzionnaire.server.common.data.Tenant;

public interface TbTenantService {

    Tenant save(Tenant tenant) throws Exception;

    void delete(Tenant tenant) throws Exception;

}
