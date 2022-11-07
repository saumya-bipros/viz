package com.vizzionnaire.server.service.transport;

import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportApiResponseMsg;
import com.vizzionnaire.server.queue.TbQueueHandler;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;

/**
 * Created by ashvayka on 05.10.18.
 */
public interface TransportApiService extends TbQueueHandler<TbProtoQueueMsg<TransportApiRequestMsg>, TbProtoQueueMsg<TransportApiResponseMsg>> {
}
