package com.vizzionnaire.server.queue.common;

import com.vizzionnaire.server.common.msg.queue.RuleEngineException;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.common.msg.queue.TbMsgCallback;
import com.vizzionnaire.server.queue.TbQueueCallback;
import com.vizzionnaire.server.queue.TbQueueMsgMetadata;

import java.util.concurrent.atomic.AtomicInteger;

public class MultipleTbQueueTbMsgCallbackWrapper implements TbQueueCallback {

    private final AtomicInteger tbQueueCallbackCount;
    private final TbMsgCallback tbMsgCallback;

    public MultipleTbQueueTbMsgCallbackWrapper(int tbQueueCallbackCount, TbMsgCallback tbMsgCallback) {
        this.tbQueueCallbackCount = new AtomicInteger(tbQueueCallbackCount);
        this.tbMsgCallback = tbMsgCallback;
    }

    @Override
    public void onSuccess(TbQueueMsgMetadata metadata) {
        if (tbQueueCallbackCount.decrementAndGet() <= 0) {
            tbMsgCallback.onSuccess();
        }
    }

    @Override
    public void onFailure(Throwable t) {
        tbMsgCallback.onFailure(new RuleEngineException(t.getMessage()));
    }
}
