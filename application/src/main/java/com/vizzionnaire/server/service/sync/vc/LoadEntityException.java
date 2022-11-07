package com.vizzionnaire.server.service.sync.vc;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Getter;

@SuppressWarnings("rawtypes")
public class LoadEntityException extends RuntimeException {

    private static final long serialVersionUID = -1749719992370409504L;
    @Getter
    private final EntityId externalId;

    public LoadEntityException(EntityId externalId, Throwable cause) {
        super(cause);
        this.externalId = externalId;
    }
}
