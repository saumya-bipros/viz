package com.vizzionnaire.rule.engine.flow;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.server.common.data.id.QueueId;

@Data
public class TbCheckpointNodeConfiguration implements NodeConfiguration<TbCheckpointNodeConfiguration> {

    private String queueName;

    @Override
    public TbCheckpointNodeConfiguration defaultConfiguration() {
        return new TbCheckpointNodeConfiguration();
    }
}
