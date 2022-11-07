package com.vizzionnaire.server.service.stats;

import com.vizzionnaire.server.service.queue.TbRuleEngineConsumerStats;

public interface RuleEngineStatisticsService {

    void reportQueueStats(long ts, TbRuleEngineConsumerStats stats);
}
