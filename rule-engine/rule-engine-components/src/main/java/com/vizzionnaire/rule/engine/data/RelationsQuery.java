package com.vizzionnaire.rule.engine.data;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;
import com.vizzionnaire.server.common.data.relation.RelationEntityTypeFilter;

@Data
public class RelationsQuery {

    private EntitySearchDirection direction;
    private int maxLevel = 1;
    private List<RelationEntityTypeFilter> filters;
    private boolean fetchLastLevelOnly = false;
}
