package com.vizzionnaire.server.common.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.id.HasId;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.UUID;

@ApiModel
@Data
public class EntityInfo implements HasId<EntityId>, HasName {

    @ApiModelProperty(position = 1, value = "JSON object with the entity Id. ")
    private final EntityId id;
    @ApiModelProperty(position = 2, value = "Entity Name")
    private final String name;

    @JsonCreator
    public EntityInfo(@JsonProperty("id") EntityId id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public EntityInfo(UUID uuid, String entityType, String name) {
        this.id = EntityIdFactory.getByTypeAndUuid(entityType, uuid);
        this.name = name;
    }

}
