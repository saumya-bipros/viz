package com.vizzionnaire.rule.engine.action;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbLogNodeConfiguration implements NodeConfiguration {

    private String jsScript;

    @Override
    public TbLogNodeConfiguration defaultConfiguration() {
        TbLogNodeConfiguration configuration = new TbLogNodeConfiguration();
        configuration.setJsScript("return '\\nIncoming message:\\n' + JSON.stringify(msg) + '\\nIncoming metadata:\\n' + JSON.stringify(metadata);");
        return configuration;
    }
}
