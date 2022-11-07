package com.vizzionnaire.server.common.data;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.HasId;
import com.vizzionnaire.server.common.data.id.TenantId;

import io.swagger.annotations.ApiModelProperty;

public interface ExportableEntity<I extends EntityId> extends HasId<I>, HasName {

    void setId(I id);

    @ApiModelProperty(position = 100, value = "JSON object with External Id from the VCS", accessMode = ApiModelProperty.AccessMode.READ_ONLY, hidden = true)
    I getExternalId();

    void setExternalId(I externalId);

    long getCreatedTime();

    void setCreatedTime(long createdTime);

    void setTenantId(TenantId tenantId);

}
