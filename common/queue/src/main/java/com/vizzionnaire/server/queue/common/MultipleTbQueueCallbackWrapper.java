package com.vizzionnaire.server.queue.common;

import com.vizzionnaire.server.common.msg.queue.RuleEngineException;
import com.vizzionnaire.server.queue.TbQueueCallback;
import com.vizzionnaire.server.queue.TbQueueMsgMetadata;

import java.util.concurrent.atomic.AtomicInteger;

public class MultipleTbQueueCallbackWrapper implements TbQueueCallback {

    private final AtomicInteger tbQueueCallbackCount;
    private final TbQueueCallback callback;

    public MultipleTbQueueCallbackWrapper(int tbQueueCallbackCount, TbQueueCallback callback) {
        this.tbQueueCallbackCount = new AtomicInteger(tbQueueCallbackCount);
        this.callback = callback;
    }

    @Override
    public void onSuccess(TbQueueMsgMetadata metadata) {
        if (tbQueueCallbackCount.decrementAndGet() <= 0) {
            callback.onSuccess(metadata);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        callback.onFailure(new RuleEngineException(t.getMessage()));
    }
}
