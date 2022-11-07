package com.vizzionnaire.server.queue.provider;

import com.vizzionnaire.server.gen.transport.TransportProtos.ToUsageStatsServiceMsg;
import com.vizzionnaire.server.queue.TbQueueProducer;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;

public interface TbUsageStatsClientQueueFactory {

    TbQueueProducer<TbProtoQueueMsg<ToUsageStatsServiceMsg>> createToUsageStatsServiceMsgProducer();

}
