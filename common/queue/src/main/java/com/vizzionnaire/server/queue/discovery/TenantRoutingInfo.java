package com.vizzionnaire.server.queue.discovery;

import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;

@Data
public class TenantRoutingInfo {
    private final TenantId tenantId;
    private final boolean isolatedTbRuleEngine;
}
