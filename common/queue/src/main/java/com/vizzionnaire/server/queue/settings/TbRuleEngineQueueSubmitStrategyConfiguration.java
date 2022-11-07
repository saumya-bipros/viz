package com.vizzionnaire.server.queue.settings;

import lombok.Data;

@Data
@Deprecated
public class TbRuleEngineQueueSubmitStrategyConfiguration {

    private String type;
    private int batchSize;

}
