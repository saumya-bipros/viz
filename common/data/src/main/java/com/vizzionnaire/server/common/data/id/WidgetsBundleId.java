package com.vizzionnaire.server.common.data.id;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.EntityType;

import io.swagger.annotations.ApiModelProperty;

public final class WidgetsBundleId extends UUIDBased implements EntityId {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public WidgetsBundleId(@JsonProperty("id") UUID id) {
        super(id);
    }

    @ApiModelProperty(position = 2, required = true, value = "string", example = "WIDGETS_BUNDLE", allowableValues = "WIDGETS_BUNDLE")
    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGETS_BUNDLE;
    }
}
