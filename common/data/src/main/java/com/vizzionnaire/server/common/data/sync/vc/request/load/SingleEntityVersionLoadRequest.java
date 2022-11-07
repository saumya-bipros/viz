package com.vizzionnaire.server.common.data.sync.vc.request.load;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SingleEntityVersionLoadRequest extends VersionLoadRequest {

    private EntityId externalEntityId;

    private VersionLoadConfig config;

    @Override
    public VersionLoadRequestType getType() {
        return VersionLoadRequestType.SINGLE_ENTITY;
    }

}
