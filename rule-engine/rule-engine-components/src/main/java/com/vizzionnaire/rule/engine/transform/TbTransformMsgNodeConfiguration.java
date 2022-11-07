package com.vizzionnaire.rule.engine.transform;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbTransformMsgNodeConfiguration extends TbTransformNodeConfiguration implements NodeConfiguration {

    private String jsScript;

    @Override
    public TbTransformMsgNodeConfiguration defaultConfiguration() {
        TbTransformMsgNodeConfiguration configuration = new TbTransformMsgNodeConfiguration();
        configuration.setJsScript("return {msg: msg, metadata: metadata, msgType: msgType};");
        return configuration;
    }
}
