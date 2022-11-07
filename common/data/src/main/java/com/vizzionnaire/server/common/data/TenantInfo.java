package com.vizzionnaire.server.common.data;

import com.vizzionnaire.server.common.data.id.TenantId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class TenantInfo extends Tenant {
    @ApiModelProperty(position = 15, value = "Tenant Profile name", example = "Default")
    private String tenantProfileName;

    public TenantInfo() {
        super();
    }

    public TenantInfo(TenantId tenantId) {
        super(tenantId);
    }

    public TenantInfo(Tenant tenant, String tenantProfileName) {
        super(tenant);
        this.tenantProfileName = tenantProfileName;
    }

}
