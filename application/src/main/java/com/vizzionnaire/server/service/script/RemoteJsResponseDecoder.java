package com.vizzionnaire.server.service.script;

import com.google.protobuf.util.JsonFormat;
import com.vizzionnaire.server.gen.js.JsInvokeProtos;
import com.vizzionnaire.server.queue.TbQueueMsg;
import com.vizzionnaire.server.queue.common.TbProtoQueueMsg;
import com.vizzionnaire.server.queue.kafka.TbKafkaDecoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by ashvayka on 25.09.18.
 */
public class RemoteJsResponseDecoder implements TbKafkaDecoder<TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse>> {

    @Override
    public TbProtoQueueMsg<JsInvokeProtos.RemoteJsResponse> decode(TbQueueMsg msg) throws IOException {
        JsInvokeProtos.RemoteJsResponse.Builder builder = JsInvokeProtos.RemoteJsResponse.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(new String(msg.getData(), StandardCharsets.UTF_8), builder);
        return new TbProtoQueueMsg<>(msg.getKey(), builder.build(), msg.getHeaders());
    }
}
