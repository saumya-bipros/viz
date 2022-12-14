package com.vizzionnaire.rule.engine.filter;

import com.google.common.collect.Sets;
import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

import java.util.Set;

@Data
public class TbJsSwitchNodeConfiguration implements NodeConfiguration<TbJsSwitchNodeConfiguration> {

    private String jsScript;

    @Override
    public TbJsSwitchNodeConfiguration defaultConfiguration() {
        TbJsSwitchNodeConfiguration configuration = new TbJsSwitchNodeConfiguration();
        configuration.setJsScript("function nextRelation(metadata, msg) {\n" +
                "    return ['one','nine'];\n" +
                "}\n" +
                "if(msgType === 'POST_TELEMETRY_REQUEST') {\n" +
                "    return ['two'];\n" +
                "}\n" +
                "return nextRelation(metadata, msg);");
        return configuration;
    }
}
