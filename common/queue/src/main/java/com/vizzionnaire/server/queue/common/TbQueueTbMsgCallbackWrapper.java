package com.vizzionnaire.server.queue.common;

import com.vizzionnaire.server.common.msg.queue.RuleEngineException;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.common.msg.queue.TbMsgCallback;
import com.vizzionnaire.server.queue.TbQueueCallback;
import com.vizzionnaire.server.queue.TbQueueMsgMetadata;

public class TbQueueTbMsgCallbackWrapper implements TbQueueCallback {

    private final TbMsgCallback tbMsgCallback;

    public TbQueueTbMsgCallbackWrapper(TbMsgCallback tbMsgCallback) {
        this.tbMsgCallback = tbMsgCallback;
    }

    @Override
    public void onSuccess(TbQueueMsgMetadata metadata) {
        tbMsgCallback.onSuccess();
    }

    @Override
    public void onFailure(Throwable t) {
        tbMsgCallback.onFailure(new RuleEngineException(t.getMessage()));
    }
}
