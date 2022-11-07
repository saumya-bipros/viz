package com.vizzionnaire.server.common.transport.profile;

import lombok.Data;

import java.util.Set;

import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.TenantId;

@Data
public class TenantProfileUpdateResult {

    private final TenantProfile profile;
    private final Set<TenantId> affectedTenants;

}
