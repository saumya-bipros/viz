package com.vizzionnaire.server.common.data.query;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Data;

@Data
public class SingleEntityFilter implements EntityFilter {
    @Override
    public EntityFilterType getType() {
        return EntityFilterType.SINGLE_ENTITY;
    }

    private EntityId singleEntity;

}
