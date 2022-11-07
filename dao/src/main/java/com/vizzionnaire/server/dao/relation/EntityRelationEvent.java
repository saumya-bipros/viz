package com.vizzionnaire.server.dao.relation;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.common.data.relation.RelationTypeGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EntityRelationEvent {
    @Getter
    private final EntityId from;
    @Getter
    private final EntityId to;
    @Getter
    private final String type;
    @Getter
    private final RelationTypeGroup typeGroup;

    public static EntityRelationEvent from(EntityRelation relation) {
        return new EntityRelationEvent(relation.getFrom(), relation.getTo(), relation.getType(), relation.getTypeGroup());
    }
}
