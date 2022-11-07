package com.vizzionnaire.server.service.script;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.AbstractListeningExecutor;

@Component
public class JsExecutorService extends AbstractListeningExecutor {

    @Value("${actors.rule.js_thread_pool_size}")
    private int jsExecutorThreadPoolSize;

    @Override
    protected int getThreadPollSize() {
        return Math.max(jsExecutorThreadPoolSize, 1);
    }

}
