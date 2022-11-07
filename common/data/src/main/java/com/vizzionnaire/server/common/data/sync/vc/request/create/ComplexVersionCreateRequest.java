package com.vizzionnaire.server.common.data.sync.vc.request.create;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

import com.vizzionnaire.server.common.data.EntityType;

@Data
@EqualsAndHashCode(callSuper = true)
public class ComplexVersionCreateRequest extends VersionCreateRequest {

    // Default sync strategy
    private SyncStrategy syncStrategy;
    private Map<EntityType, EntityTypeVersionCreateConfig> entityTypes;

    @Override
    public VersionCreateRequestType getType() {
        return VersionCreateRequestType.COMPLEX;
    }

}
