package com.vizzionnaire.server.common.data.widget;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.server.common.data.id.WidgetTypeId;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WidgetType extends BaseWidgetType {

    @ApiModelProperty(position = 7, value = "Complex JSON object that describes the widget type", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private transient JsonNode descriptor;

    public WidgetType() {
        super();
    }

    public WidgetType(WidgetTypeId id) {
        super(id);
    }

    public WidgetType(BaseWidgetType baseWidgetType) {
        super(baseWidgetType);
    }

    public WidgetType(WidgetType widgetType) {
        super(widgetType);
        this.descriptor = widgetType.getDescriptor();
    }

}
