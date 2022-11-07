package com.vizzionnaire.rule.engine.action;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbMsgCountNodeConfiguration implements NodeConfiguration<TbMsgCountNodeConfiguration> {

    private String telemetryPrefix;
    private int interval;

    @Override
    public TbMsgCountNodeConfiguration defaultConfiguration() {
        TbMsgCountNodeConfiguration configuration = new TbMsgCountNodeConfiguration();
        configuration.setInterval(1);
        configuration.setTelemetryPrefix("messageCount");
        return configuration;
    }
}
