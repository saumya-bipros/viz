package com.vizzionnaire.server.common.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.EntityType;

import io.swagger.annotations.ApiModelProperty;

public class TenantProfileId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public TenantProfileId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static TenantProfileId fromString(String tenantProfileId) {
        return new TenantProfileId(UUID.fromString(tenantProfileId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "TENANT_PROFILE", allowableValues = "TENANT_PROFILE")
    @Override
    public EntityType getEntityType() {
        return EntityType.TENANT_PROFILE;
    }
}
