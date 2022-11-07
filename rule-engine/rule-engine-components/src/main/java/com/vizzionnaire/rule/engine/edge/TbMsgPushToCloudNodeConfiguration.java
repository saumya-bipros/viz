package com.vizzionnaire.rule.engine.edge;

import com.vizzionnaire.server.common.data.DataConstants;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TbMsgPushToCloudNodeConfiguration extends BaseTbMsgPushNodeConfiguration {

    @Override
    public TbMsgPushToCloudNodeConfiguration defaultConfiguration() {
        TbMsgPushToCloudNodeConfiguration configuration = new TbMsgPushToCloudNodeConfiguration();
        configuration.setScope(DataConstants.SERVER_SCOPE);
        return configuration;
    }
}
