package com.vizzionnaire.server.common.data.query;

import com.vizzionnaire.server.common.data.EntityType;

import lombok.Data;

@Data
public class EntityNameFilter implements EntityFilter {
    @Override
    public EntityFilterType getType() {
        return EntityFilterType.ENTITY_NAME;
    }

    private EntityType entityType;

    private String entityNameFilter;

}
