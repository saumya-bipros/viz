package com.vizzionnaire.server.dao.asset;

import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class AssetCacheEvictEvent {

    private final TenantId tenantId;
    private final String newName;
    private final String oldName;

}
