package com.vizzionnaire.rule.engine.metadata;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
public class TbGetEntityAttrNodeConfiguration implements NodeConfiguration<TbGetEntityAttrNodeConfiguration> {

    private Map<String, String> attrMapping;
    private boolean isTelemetry = false;

    @Override
    public TbGetEntityAttrNodeConfiguration defaultConfiguration() {
        TbGetEntityAttrNodeConfiguration configuration = new TbGetEntityAttrNodeConfiguration();
        Map<String, String> attrMapping = new HashMap<>();
        attrMapping.putIfAbsent("temperature", "tempo");
        configuration.setAttrMapping(attrMapping);
        configuration.setTelemetry(false);
        return configuration;
    }
}
