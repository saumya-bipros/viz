package com.vizzionnaire.server.actors;

import lombok.RequiredArgsConstructor;

import com.vizzionnaire.server.actors.TbActorId;
import com.vizzionnaire.server.actors.TbEntityActorId;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class TbEntityTypeActorIdPredicate implements Predicate<TbActorId> {

    private final EntityType entityType;

    @Override
    public boolean test(TbActorId actorId) {
        return actorId instanceof TbEntityActorId && testEntityId(((TbEntityActorId) actorId).getEntityId());
    }

    protected boolean testEntityId(EntityId entityId) {
        return entityId.getEntityType().equals(entityType);
    }
}
