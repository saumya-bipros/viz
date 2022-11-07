package com.vizzionnaire.server.transport.lwm2m.server.common;

import java.util.concurrent.ExecutorService;

import com.vizzionnaire.common.util.VizzionnaireExecutors;

public abstract class LwM2MExecutorAwareService {

    protected ExecutorService executor;

    protected abstract int getExecutorSize();

    protected abstract String getExecutorName();

    protected void init() {
        this.executor = VizzionnaireExecutors.newWorkStealingPool(getExecutorSize(), getExecutorName());
    }

    public void destroy() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

}
