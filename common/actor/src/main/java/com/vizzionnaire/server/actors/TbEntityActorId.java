package com.vizzionnaire.server.actors;

import lombok.Getter;

import java.util.Objects;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;

public class TbEntityActorId implements TbActorId {

    @Getter
    private final EntityId entityId;

    public TbEntityActorId(EntityId entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return entityId.getEntityType() + "|" + entityId.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbEntityActorId that = (TbEntityActorId) o;
        return entityId.equals(that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId);
    }

    @Override
    public EntityType getEntityType() {
        return entityId.getEntityType();
    }
}
