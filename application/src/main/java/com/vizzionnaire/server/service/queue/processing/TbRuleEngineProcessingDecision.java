package com.vizzionnaire.server.service.queue.processing;

import lombok.Data;

import com.vizzionnaire.server.gen.transport.TransportProtos.ToRuleEngineMsg;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

@Data
public class TbRuleEngineProcessingDecision {

    private final boolean commit;
    private final ConcurrentMap<UUID, TbProtoQueueMsg<ToRuleEngineMsg>> reprocessMap;

}
