package com.vizzionnaire.rule.engine.filter;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbJsFilterNodeConfiguration implements NodeConfiguration<TbJsFilterNodeConfiguration> {

    private String jsScript;

    @Override
    public TbJsFilterNodeConfiguration defaultConfiguration() {
        TbJsFilterNodeConfiguration configuration = new TbJsFilterNodeConfiguration();
        configuration.setJsScript("return msg.temperature > 20;");
        return configuration;
    }
}
