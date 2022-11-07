package com.vizzionnaire.server.common.data.sync.ie;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.widget.BaseWidgetType;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class WidgetsBundleExportData extends EntityExportData<WidgetsBundle> {

    @JsonProperty(index = 3)
    private List<WidgetTypeDetails> widgets;

    @Override
    public EntityExportData<WidgetsBundle> sort() {
        super.sort();
        widgets.sort(Comparator.comparing(BaseWidgetType::getAlias));
        return this;
    }

}
