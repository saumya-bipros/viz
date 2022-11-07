package com.vizzionnaire.server.common.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.EntityType;

import io.swagger.annotations.ApiModelProperty;

public class UserId extends UUIDBased implements EntityId {

    @JsonCreator
    public UserId(@JsonProperty("id") UUID id) {
        super(id);
    }

    public static UserId fromString(String userId) {
        return new UserId(UUID.fromString(userId));
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "USER", allowableValues = "USER")
    @Override
    public EntityType getEntityType() {
        return EntityType.USER;
    }

}
