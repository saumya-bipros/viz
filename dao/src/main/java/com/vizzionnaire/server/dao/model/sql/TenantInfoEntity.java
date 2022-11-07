package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

import com.vizzionnaire.server.common.data.TenantInfo;

@Data
@EqualsAndHashCode(callSuper = true)
public class TenantInfoEntity extends AbstractTenantEntity<TenantInfo> {

    public static final Map<String,String> tenantInfoColumnMap = new HashMap<>();
    static {
        tenantInfoColumnMap.put("tenantProfileName", "p.name");
    }

    private String tenantProfileName;

    public TenantInfoEntity() {
        super();
    }

    public TenantInfoEntity(TenantEntity tenantEntity, String tenantProfileName) {
        super(tenantEntity);
        this.tenantProfileName = tenantProfileName;
    }

    @Override
    public TenantInfo toData() {
        return new TenantInfo(super.toTenant(), this.tenantProfileName);
    }
}
