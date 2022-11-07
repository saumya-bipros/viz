package com.vizzionnaire.server.common.data.query;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;

@Data
public class EntityListFilter implements EntityFilter {
    @Override
    public EntityFilterType getType() {
        return EntityFilterType.ENTITY_LIST;
    }

    private EntityType entityType;

    private List<String> entityList;

}
