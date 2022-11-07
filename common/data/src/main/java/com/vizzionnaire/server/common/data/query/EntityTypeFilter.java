package com.vizzionnaire.server.common.data.query;

import com.vizzionnaire.server.common.data.EntityType;

import lombok.Data;

@Data
public class EntityTypeFilter implements EntityFilter {
    @Override
    public EntityFilterType getType() {
        return EntityFilterType.ENTITY_TYPE;
    }

    private EntityType entityType;

}
