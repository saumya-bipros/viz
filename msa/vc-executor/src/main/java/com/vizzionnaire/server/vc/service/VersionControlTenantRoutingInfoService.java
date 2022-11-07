package com.vizzionnaire.server.vc.service;

import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.queue.discovery.TenantRoutingInfo;
import com.vizzionnaire.server.queue.discovery.TenantRoutingInfoService;

@Service
public class VersionControlTenantRoutingInfoService implements TenantRoutingInfoService {
    @Override
    public TenantRoutingInfo getRoutingInfo(TenantId tenantId) {
        //This dummy implementation is ok since Version Control service does not produce any rule engine messages.
        return new TenantRoutingInfo(tenantId, false);
    }
}
