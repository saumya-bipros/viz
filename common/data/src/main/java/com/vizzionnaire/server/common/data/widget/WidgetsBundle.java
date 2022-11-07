package com.vizzionnaire.server.common.data.widget;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vizzionnaire.server.common.data.ExportableEntity;
import com.vizzionnaire.server.common.data.HasTenantId;
import com.vizzionnaire.server.common.data.SearchTextBased;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.WidgetsBundleId;
import com.vizzionnaire.server.common.data.validation.Length;
import com.vizzionnaire.server.common.data.validation.NoXss;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@EqualsAndHashCode(callSuper = true)
public class WidgetsBundle extends SearchTextBased<WidgetsBundleId> implements HasTenantId, ExportableEntity<WidgetsBundleId> {

    private static final long serialVersionUID = -7627368878362410489L;

    @Getter
    @Setter
    @ApiModelProperty(position = 3, value = "JSON object with Tenant Id.", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private TenantId tenantId;

    @NoXss
    @Length(fieldName = "alias")
    @Getter
    @Setter
    @ApiModelProperty(position = 4, value = "Unique alias that is used in widget types as a reference widget bundle", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String alias;

    @NoXss
    @Length(fieldName = "title")
    @Getter
    @Setter
    @ApiModelProperty(position = 5, value = "Title used in search and UI", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String title;

    @Length(fieldName = "image", max = 1000000)
    @Getter
    @Setter
    @ApiModelProperty(position = 6, value = "Base64 encoded thumbnail", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String image;

    @NoXss
    @Length(fieldName = "description")
    @Getter
    @Setter
    @ApiModelProperty(position = 7, value = "Description", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    private String description;

    @Getter
    @Setter
    private WidgetsBundleId externalId;

    public WidgetsBundle() {
        super();
    }

    public WidgetsBundle(WidgetsBundleId id) {
        super(id);
    }

    public WidgetsBundle(WidgetsBundle widgetsBundle) {
        super(widgetsBundle);
        this.tenantId = widgetsBundle.getTenantId();
        this.alias = widgetsBundle.getAlias();
        this.title = widgetsBundle.getTitle();
        this.image = widgetsBundle.getImage();
        this.description = widgetsBundle.getDescription();
        this.externalId = widgetsBundle.getExternalId();
    }

    @ApiModelProperty(position = 1, value = "JSON object with the Widget Bundle Id. " +
            "Specify this field to update the Widget Bundle. " +
            "Referencing non-existing Widget Bundle Id will cause error. " +
            "Omit this field to create new Widget Bundle." )
    @Override
    public WidgetsBundleId getId() {
        return super.getId();
    }

    @ApiModelProperty(position = 2, value = "Timestamp of the Widget Bundle creation, in milliseconds", example = "1609459200000", accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    @Override
    public long getCreatedTime() {
        return super.getCreatedTime();
    }

    @Override
    public String getSearchText() {
        return getTitle();
    }

    @JsonIgnore
    @Override
    public String getName() {
        return title;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("WidgetsBundle{");
        sb.append("tenantId=").append(tenantId);
        sb.append(", alias='").append(alias).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }

}