package com.vizzionnaire.server.common.data.widget;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.vizzionnaire.server.common.data.id.WidgetTypeId;
import com.vizzionnaire.server.common.data.validation.Length;
import com.vizzionnaire.server.common.data.validation.NoXss;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@JsonPropertyOrder({ "alias", "name", "image", "description", "descriptor" })
public class WidgetTypeDetails extends WidgetType {

    @Length(fieldName = "image", max = 1000000)
    @ApiModelProperty(position = 8, value = "Base64 encoded thumbnail", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String image;
    @NoXss
    @Length(fieldName = "description")
    @ApiModelProperty(position = 9, value = "Description of the widget", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String description;

    public WidgetTypeDetails() {
        super();
    }

    public WidgetTypeDetails(WidgetTypeId id) {
        super(id);
    }

    public WidgetTypeDetails(BaseWidgetType baseWidgetType) {
        super(baseWidgetType);
    }

    public WidgetTypeDetails(WidgetTypeDetails widgetTypeDetails) {
        super(widgetTypeDetails);
        this.image = widgetTypeDetails.getImage();
        this.description = widgetTypeDetails.getDescription();
    }
}
