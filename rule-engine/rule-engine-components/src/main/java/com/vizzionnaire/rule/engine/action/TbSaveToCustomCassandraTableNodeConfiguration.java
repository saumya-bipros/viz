package com.vizzionnaire.rule.engine.action;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
public class TbSaveToCustomCassandraTableNodeConfiguration implements NodeConfiguration<TbSaveToCustomCassandraTableNodeConfiguration> {


    private String tableName;
    private Map<String, String> fieldsMapping;


    @Override
    public TbSaveToCustomCassandraTableNodeConfiguration defaultConfiguration() {
        TbSaveToCustomCassandraTableNodeConfiguration configuration = new TbSaveToCustomCassandraTableNodeConfiguration();
        configuration.setTableName("");
        Map<String, String> map = new HashMap<>();
        map.put("", "");
        configuration.setFieldsMapping(map);
        return configuration;
    }
}
