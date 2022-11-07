package com.vizzionnaire.server.common.data.sync;

import com.vizzionnaire.server.common.data.exception.ThingsboardException;

public interface ThrowingRunnable {

    void run() throws ThingsboardException;

    default ThrowingRunnable andThen(ThrowingRunnable after) {
        return () -> {
            this.run();
            after.run();
        };
    }

}
