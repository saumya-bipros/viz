package com.vizzionnaire.server.common.data.sync;

import com.vizzionnaire.server.common.data.exception.VizzionnaireException;

public interface ThrowingRunnable {

    void run() throws VizzionnaireException;

    default ThrowingRunnable andThen(ThrowingRunnable after) {
        return () -> {
            this.run();
            after.run();
        };
    }

}
