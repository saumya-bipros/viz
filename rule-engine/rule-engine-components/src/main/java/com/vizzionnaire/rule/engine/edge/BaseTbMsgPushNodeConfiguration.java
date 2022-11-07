package com.vizzionnaire.rule.engine.edge;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.server.common.data.DataConstants;

@Data
public class BaseTbMsgPushNodeConfiguration implements NodeConfiguration<BaseTbMsgPushNodeConfiguration> {

    private String scope;

    @Override
    public BaseTbMsgPushNodeConfiguration defaultConfiguration() {
        BaseTbMsgPushNodeConfiguration configuration = new BaseTbMsgPushNodeConfiguration();
        configuration.setScope(DataConstants.SERVER_SCOPE);
        return configuration;
    }
}
