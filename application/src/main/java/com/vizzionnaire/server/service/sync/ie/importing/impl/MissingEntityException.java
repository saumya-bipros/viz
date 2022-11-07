package com.vizzionnaire.server.service.sync.ie.importing.impl;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Getter;

public class MissingEntityException extends ImportServiceException {

    private static final long serialVersionUID = 3669135386955906022L;
    @Getter
    private final EntityId entityId;

    public MissingEntityException(EntityId entityId) {
        this.entityId = entityId;
    }
}
