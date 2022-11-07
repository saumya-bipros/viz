package com.vizzionnaire.server.dao.util;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.dao.util.AsyncTask;

/**
 * Created by ashvayka on 24.10.18.
 */
public interface BufferedRateExecutor<T extends AsyncTask, F extends ListenableFuture> {

    F submit(T task);

}
