package com.vizzionnaire.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.EntityType;

import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

/**
 * Created by Victor Basanets on 8/27/2017.
 */
public class EntityViewId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public EntityViewId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static EntityViewId fromString(String entityViewID) {
        return new EntityViewId(UUID.fromString(entityViewID));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "ENTITY_VIEW", allowableValues = "ENTITY_VIEW")
    @Override
    public EntityType getEntityType() {
        return EntityType.ENTITY_VIEW;
    }
}
