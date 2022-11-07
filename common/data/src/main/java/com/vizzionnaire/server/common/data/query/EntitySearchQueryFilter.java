package com.vizzionnaire.server.common.data.query;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;

import lombok.Data;

@Data
public abstract class EntitySearchQueryFilter implements EntityFilter {

    private EntityId rootEntity;
    private String relationType;
    private EntitySearchDirection direction;
    private int maxLevel;
    private boolean fetchLastLevelOnly;

}
