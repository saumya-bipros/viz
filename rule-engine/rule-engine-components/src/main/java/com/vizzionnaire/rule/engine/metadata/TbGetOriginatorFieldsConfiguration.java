package com.vizzionnaire.rule.engine.metadata;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
public class TbGetOriginatorFieldsConfiguration implements NodeConfiguration<TbGetOriginatorFieldsConfiguration> {

    private Map<String, String> fieldsMapping;

    @Override
    public TbGetOriginatorFieldsConfiguration defaultConfiguration() {
        TbGetOriginatorFieldsConfiguration configuration = new TbGetOriginatorFieldsConfiguration();
        Map<String, String> fieldsMapping = new HashMap<>();
        fieldsMapping.put("name", "originatorName");
        fieldsMapping.put("type", "originatorType");
        configuration.setFieldsMapping(fieldsMapping);
        return configuration;
    }
}
