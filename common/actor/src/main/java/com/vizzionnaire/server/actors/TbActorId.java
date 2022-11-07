package com.vizzionnaire.server.actors;

import com.vizzionnaire.server.common.data.EntityType;

public interface TbActorId {

    /**
     * Returns entity type of the actor.
     * May return null if the actor does not belong to any entity.
     * This method is added for performance optimization.
     *
     */
    EntityType getEntityType();

}
