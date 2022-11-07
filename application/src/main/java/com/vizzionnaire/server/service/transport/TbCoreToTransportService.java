package com.vizzionnaire.server.service.transport;

import java.util.function.Consumer;

import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportMsg;

public interface TbCoreToTransportService {

    void process(String nodeId, ToTransportMsg msg);

    void process(String nodeId, ToTransportMsg msg, Runnable onSuccess, Consumer<Throwable> onFailure);

}
