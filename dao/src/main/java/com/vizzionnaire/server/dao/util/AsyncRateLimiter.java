package com.vizzionnaire.server.dao.util;

import com.google.common.util.concurrent.ListenableFuture;

public interface AsyncRateLimiter {

    ListenableFuture<Void> acquireAsync();

    void release();
}
