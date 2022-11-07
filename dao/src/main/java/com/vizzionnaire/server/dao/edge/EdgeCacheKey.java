package com.vizzionnaire.server.dao.edge;

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
public class EdgeCacheKey implements Serializable {

    private final TenantId tenantId;
    private final String name;

    @Override
    public String toString() {
        return tenantId + "_" + name;
    }

}
