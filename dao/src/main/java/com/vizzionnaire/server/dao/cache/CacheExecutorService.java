package com.vizzionnaire.server.dao.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.AbstractListeningExecutor;

@Component
public class CacheExecutorService extends AbstractListeningExecutor {

    @Value("${cache.maximumPoolSize}")
    private int poolSize;

    @Override
    protected int getThreadPollSize() {
        return poolSize;
    }

}
