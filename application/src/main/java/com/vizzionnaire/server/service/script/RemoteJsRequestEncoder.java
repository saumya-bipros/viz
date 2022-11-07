package com.vizzionnaire.server.service.script;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.vizzionnaire.server.gen.js.JsInvokeProtos;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.kafka.TbKafkaEncoder;

import java.nio.charset.StandardCharsets;

/**
 * Created by ashvayka on 25.09.18.
 */
public class RemoteJsRequestEncoder implements TbKafkaEncoder<TbProtoQueueMsg<JsInvokeProtos.RemoteJsRequest>> {
    @Override
    public byte[] encode(TbProtoQueueMsg<JsInvokeProtos.RemoteJsRequest> value) {
        try {
            return JsonFormat.printer().print(value.getValue()).getBytes(StandardCharsets.UTF_8);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }
}
