package com.vizzionnaire.rule.engine.profile;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TbDeviceProfileNodeConfiguration implements NodeConfiguration<TbDeviceProfileNodeConfiguration> {

    private boolean persistAlarmRulesState;
    private boolean fetchAlarmRulesStateOnStart;

    @Override
    public TbDeviceProfileNodeConfiguration defaultConfiguration() {
        return new TbDeviceProfileNodeConfiguration();
    }
}
