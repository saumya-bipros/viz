package com.vizzionnaire.server.queue;

import com.google.protobuf.InvalidProtocolBufferException;
import com.vizzionnaire.server.queue.TbQueueMsg;

public interface TbQueueMsgDecoder<T extends TbQueueMsg> {

    T decode(TbQueueMsg msg) throws InvalidProtocolBufferException;
}
