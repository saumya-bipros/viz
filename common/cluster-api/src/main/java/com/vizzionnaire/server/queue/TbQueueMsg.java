package com.vizzionnaire.server.queue;

import java.util.UUID;

public interface TbQueueMsg {

    UUID getKey();

    TbQueueMsgHeaders getHeaders();

    byte[] getData();
}
