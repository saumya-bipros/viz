package com.vizzionnaire.server.common.data.query;

import lombok.Data;

import java.util.List;
import java.util.Set;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;
import com.vizzionnaire.server.common.data.relation.RelationEntityTypeFilter;

@Data
public class RelationsQueryFilter implements EntityFilter {

    @Override
    public EntityFilterType getType() {
        return EntityFilterType.RELATIONS_QUERY;
    }

    private EntityId rootEntity;
    private boolean isMultiRoot;
    private EntityType multiRootEntitiesType;
    private Set<String> multiRootEntityIds;
    private EntitySearchDirection direction;
    private List<RelationEntityTypeFilter> filters;
    private int maxLevel;
    private boolean fetchLastLevelOnly;

}
