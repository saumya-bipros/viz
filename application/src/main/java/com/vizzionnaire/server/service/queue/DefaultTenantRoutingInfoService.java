package com.vizzionnaire.server.service.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.queue.discovery.TenantRoutingInfo;
import com.vizzionnaire.server.queue.discovery.TenantRoutingInfoService;

@Slf4j
@Service
@ConditionalOnExpression("'${service.type:null}'=='monolith' || '${service.type:null}'=='tb-core' || '${service.type:null}'=='tb-rule-engine'")
public class DefaultTenantRoutingInfoService implements TenantRoutingInfoService {

    private final TenantService tenantService;

    private final TbTenantProfileCache tenantProfileCache;

    public DefaultTenantRoutingInfoService(TenantService tenantService, TbTenantProfileCache tenantProfileCache) {
        this.tenantService = tenantService;
        this.tenantProfileCache = tenantProfileCache;
    }

    @Override
    public TenantRoutingInfo getRoutingInfo(TenantId tenantId) {
        TenantProfile tenantProfile = tenantProfileCache.get(tenantId);
        if (tenantProfile != null) {
            return new TenantRoutingInfo(tenantId, tenantProfile.isIsolatedTbRuleEngine());
        } else {
            throw new RuntimeException("Tenant not found!");
        }
    }
}
