package com.vizzionnaire.server.common.data.sync.vc.request.create;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SingleEntityVersionCreateRequest extends VersionCreateRequest {

    private EntityId entityId;
    private VersionCreateConfig config;

    @Override
    public VersionCreateRequestType getType() {
        return VersionCreateRequestType.SINGLE_ENTITY;
    }

}
