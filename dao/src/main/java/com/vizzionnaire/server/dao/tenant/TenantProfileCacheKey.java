package com.vizzionnaire.server.dao.tenant;

import lombok.Data;

import java.io.Serializable;

import com.vizzionnaire.server.common.data.id.TenantProfileId;

@Data
public class TenantProfileCacheKey implements Serializable {

    private static final long serialVersionUID = 8220455917177676472L;

    private final TenantProfileId tenantProfileId;
    private final boolean defaultProfile;

    private TenantProfileCacheKey(TenantProfileId tenantProfileId, boolean defaultProfile) {
        this.tenantProfileId = tenantProfileId;
        this.defaultProfile = defaultProfile;
    }

    public static TenantProfileCacheKey fromId(TenantProfileId id) {
        return new TenantProfileCacheKey(id, false);
    }

    public static TenantProfileCacheKey defaultProfile() {
        return new TenantProfileCacheKey(null, true);
    }


    @Override
    public String toString() {
        if (defaultProfile) {
            return "default";
        } else {
            return tenantProfileId.toString();
        }
    }
}
