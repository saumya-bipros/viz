package com.vizzionnaire.server.queue.common;

import lombok.Data;

import java.util.UUID;

import com.vizzionnaire.server.queue.TbQueueMsg;

@Data
public class DefaultTbQueueMsg implements TbQueueMsg {
    private final UUID key;
    private final byte[] data;
    private final DefaultTbQueueMsgHeaders headers;

    public DefaultTbQueueMsg(TbQueueMsg msg) {
        this.key = msg.getKey();
        this.data = msg.getData();
        DefaultTbQueueMsgHeaders headers = new DefaultTbQueueMsgHeaders();
        msg.getHeaders().getData().forEach(headers::put);
        this.headers = headers;
    }

}
