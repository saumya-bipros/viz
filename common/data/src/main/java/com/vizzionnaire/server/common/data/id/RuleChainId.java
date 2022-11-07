package com.vizzionnaire.server.common.data.id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.EntityType;

import io.swagger.annotations.ApiModelProperty;

import java.util.UUID;

public class RuleChainId extends UUIDBased implements EntityId {

    @JsonCreator
    public RuleChainId(@JsonProperty("id") UUID id) {
        super(id);
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "RULE_CHAIN", allowableValues = "RULE_CHAIN")
    @Override
    public EntityType getEntityType() {
        return EntityType.RULE_CHAIN;
    }
}
