package com.vizzionnaire.server.common.transport;

import com.google.protobuf.ByteString;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.TenantProfileId;
import com.vizzionnaire.server.common.transport.profile.TenantProfileUpdateResult;
import com.vizzionnaire.server.queue.discovery.TenantRoutingInfo;
import com.vizzionnaire.server.queue.discovery.TenantRoutingInfoService;

import java.util.Set;

public interface TransportTenantProfileCache {

    TenantProfile get(TenantId tenantId);

    TenantProfileUpdateResult put(ByteString profileBody);

    boolean put(TenantId tenantId, TenantProfileId profileId);

    Set<TenantId> remove(TenantProfileId profileId);

}
