package com.vizzionnaire.server.queue.common;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;
import com.vizzionnaire.server.queue.TbQueueConsumer;
import com.vizzionnaire.server.queue.TbQueueMsg;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Slf4j
public abstract class AbstractTbQueueConsumerTemplate<R, T extends TbQueueMsg> implements TbQueueConsumer<T> {

    public static final long ONE_MILLISECOND_IN_NANOS = TimeUnit.MILLISECONDS.toNanos(1);
    private volatile boolean subscribed;
    protected volatile boolean stopped = false;
    protected volatile Set<TopicPartitionInfo> partitions;
    protected final ReentrantLock consumerLock = new ReentrantLock(); //NonfairSync
    final Queue<Set<TopicPartitionInfo>> subscribeQueue = new ConcurrentLinkedQueue<>();

    @Getter
    private final String topic;

    public AbstractTbQueueConsumerTemplate(String topic) {
        this.topic = topic;
    }

    @Override
    public void subscribe() {
        log.info("enqueue topic subscribe {} ", topic);
        if (stopped) {
            log.error("trying subscribe, but consumer stopped for topic {}", topic);
            return;
        }
        subscribeQueue.add(Collections.singleton(new TopicPartitionInfo(topic, null, null, true)));
    }

    @Override
    public void subscribe(Set<TopicPartitionInfo> partitions) {
        log.info("enqueue topics subscribe {} ", partitions);
        if (stopped) {
            log.error("trying subscribe, but consumer stopped for topic {}", topic);
            return;
        }
        subscribeQueue.add(partitions);
    }

    @Override
    public List<T> poll(long durationInMillis) {
        List<R> records;
        long startNanos = System.nanoTime();
        if (stopped) {
            return errorAndReturnEmpty();
        }
        if (!subscribed && partitions == null && subscribeQueue.isEmpty()) {
            return sleepAndReturnEmpty(startNanos, durationInMillis);
        }

        if (consumerLock.isLocked()) {
            log.error("poll. consumerLock is locked. will wait with no timeout. it looks like a race conditions or deadlock topic " + topic, new RuntimeException("stacktrace"));
        }

        consumerLock.lock();
        try {
            while (!subscribeQueue.isEmpty()) {
                subscribed = false;
                partitions = subscribeQueue.poll();
            }
            if (!subscribed) {
                List<String> topicNames = partitions.stream().map(TopicPartitionInfo::getFullTopicName).collect(Collectors.toList());
                doSubscribe(topicNames);
                subscribed = true;
            }
            records = partitions.isEmpty() ? emptyList() : doPoll(durationInMillis);
        } finally {
            consumerLock.unlock();
        }

        if (records.isEmpty()) { return sleepAndReturnEmpty(startNanos, durationInMillis); }

        return decodeRecords(records);
    }

    @Nonnull
    List<T> decodeRecords(@Nonnull List<R> records) {
        List<T> result = new ArrayList<>(records.size());
        records.forEach(record -> {
            try {
                if (record != null) {
                    result.add(decode(record));
                }
            } catch (IOException e) {
                log.error("Failed decode record: [{}]", record);
                throw new RuntimeException("Failed to decode record: ", e);
            }
        });
        return result;
    }

    List<T> errorAndReturnEmpty() {
        log.error("poll invoked but consumer stopped for topic" + topic, new RuntimeException("stacktrace"));
        return emptyList();
    }

    List<T> sleepAndReturnEmpty(final long startNanos, final long durationInMillis) {
        long durationNanos = TimeUnit.MILLISECONDS.toNanos(durationInMillis);
        long spentNanos = System.nanoTime() - startNanos;
        long nanosLeft = durationNanos - spentNanos;
        if (nanosLeft >= ONE_MILLISECOND_IN_NANOS) {
            try {
                long sleepMs = TimeUnit.NANOSECONDS.toMillis(nanosLeft);
                log.trace("Going to sleep after poll: topic {} for {}ms", topic, sleepMs);
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                if (!stopped) {
                    log.error("Failed to wait", e);
                }
            }
        }
        return emptyList();
    }

    @Override
    public void commit() {
        if (consumerLock.isLocked()) {
            log.error("commit. consumerLock is locked. will wait with no timeout. it looks like a race conditions or deadlock topic " + topic, new RuntimeException("stacktrace"));
        }
        consumerLock.lock();
        try {
            doCommit();
        } finally {
            consumerLock.unlock();
        }
    }

    @Override
    public void unsubscribe() {
        log.info("unsubscribe topic and stop consumer {}", getTopic());
        stopped = true;
        consumerLock.lock();
        try {
            doUnsubscribe();
        } finally {
            consumerLock.unlock();
        }
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    abstract protected List<R> doPoll(long durationInMillis);

    abstract protected T decode(R record) throws IOException;

    abstract protected void doSubscribe(List<String> topicNames);

    abstract protected void doCommit();

    abstract protected void doUnsubscribe();

}
