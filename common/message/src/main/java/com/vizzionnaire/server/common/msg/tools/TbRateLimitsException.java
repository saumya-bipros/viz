package com.vizzionnaire.server.common.msg.tools;

import com.vizzionnaire.server.common.data.EntityType;

import lombok.Getter;

/**
 * Created by ashvayka on 22.10.18.
 */
public class TbRateLimitsException extends RuntimeException {
    @Getter
    private final EntityType entityType;

    public TbRateLimitsException(EntityType entityType) {
        super(entityType.name() + " rate limits reached!");
        this.entityType = entityType;
    }
}
