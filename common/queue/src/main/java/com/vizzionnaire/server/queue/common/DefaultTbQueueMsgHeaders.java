package com.vizzionnaire.server.queue.common;

import java.util.HashMap;
import java.util.Map;

import com.vizzionnaire.server.queue.TbQueueMsgHeaders;

public class DefaultTbQueueMsgHeaders implements TbQueueMsgHeaders {

    protected final Map<String, byte[]> data = new HashMap<>();

    @Override
    public byte[] put(String key, byte[] value) {
        return data.put(key, value);
    }

    @Override
    public byte[] get(String key) {
        return data.get(key);
    }

    @Override
    public Map<String, byte[]> getData() {
        return data;
    }
}
