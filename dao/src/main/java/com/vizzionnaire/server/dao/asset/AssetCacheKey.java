package com.vizzionnaire.server.dao.asset;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.id.TenantId;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@Builder
public class AssetCacheKey implements Serializable {

    private static final long serialVersionUID = 4196610233744512673L;

    private final TenantId tenantId;
    private final String name;

    @Override
    public String toString() {
        return tenantId + "_" + name;
    }

}
