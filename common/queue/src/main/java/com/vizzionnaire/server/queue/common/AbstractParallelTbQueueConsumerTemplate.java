package com.vizzionnaire.server.queue.common;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.common.util.VizzionnaireThreadFactory;
import com.vizzionnaire.server.queue.TbQueueMsg;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractParallelTbQueueConsumerTemplate<R, T extends TbQueueMsg> extends AbstractTbQueueConsumerTemplate<R, T> {

    protected ListeningExecutorService consumerExecutor;

    public AbstractParallelTbQueueConsumerTemplate(String topic) {
        super(topic);
    }

    protected void initNewExecutor(int threadPoolSize) {
        if (consumerExecutor != null) {
            consumerExecutor.shutdown();
            try {
                consumerExecutor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                log.trace("Interrupted while waiting for consumer executor to stop");
            }
        }
        consumerExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(threadPoolSize, VizzionnaireThreadFactory.forName(getClass().getSimpleName())));
    }

    protected void shutdownExecutor() {
        if (consumerExecutor != null) {
            consumerExecutor.shutdownNow();
        }
    }

}
