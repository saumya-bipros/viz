package com.vizzionnaire.rule.engine.util;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Data;

@Data
public class EntityContainer {

    private EntityId entityId;
    private EntityType entityType;

}