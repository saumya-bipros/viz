package com.vizzionnaire.server.dao.sqlts;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.kv.Aggregation;
import com.vizzionnaire.server.common.data.kv.BaseReadTsKvQuery;
import com.vizzionnaire.server.common.data.kv.DeleteTsKvQuery;
import com.vizzionnaire.server.common.data.kv.ReadTsKvQuery;
import com.vizzionnaire.server.common.data.kv.TsKvEntry;
import com.vizzionnaire.server.common.stats.StatsFactory;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.AbstractTsKvEntity;
import com.vizzionnaire.server.dao.model.sqlts.ts.TsKvEntity;
import com.vizzionnaire.server.dao.sql.TbSqlBlockingQueueParams;
import com.vizzionnaire.server.dao.sql.TbSqlBlockingQueueWrapper;
import com.vizzionnaire.server.dao.sqlts.insert.InsertTsRepository;
import com.vizzionnaire.server.dao.sqlts.ts.TsKvRepository;
import com.vizzionnaire.server.dao.timeseries.TimeseriesDao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractChunkedAggregationTimeseriesDao extends AbstractSqlTimeseriesDao implements TimeseriesDao {

    @Autowired
    protected TsKvRepository tsKvRepository;

    @Autowired
    protected InsertTsRepository<TsKvEntity> insertRepository;

    protected TbSqlBlockingQueueWrapper<TsKvEntity> tsQueue;
    @Autowired
    private StatsFactory statsFactory;

    @PostConstruct
    protected void init() {
        TbSqlBlockingQueueParams tsParams = TbSqlBlockingQueueParams.builder()
                .logName("TS")
                .batchSize(tsBatchSize)
                .maxDelay(tsMaxDelay)
                .statsPrintIntervalMs(tsStatsPrintIntervalMs)
                .statsNamePrefix("ts")
                .batchSortEnabled(batchSortEnabled)
                .build();

        Function<TsKvEntity, Integer> hashcodeFunction = entity -> entity.getEntityId().hashCode();
        tsQueue = new TbSqlBlockingQueueWrapper<>(tsParams, hashcodeFunction, tsBatchThreads, statsFactory);
        tsQueue.init(logExecutor, v -> insertRepository.saveOrUpdate(v),
                Comparator.comparing((Function<TsKvEntity, UUID>) AbstractTsKvEntity::getEntityId)
                        .thenComparing(AbstractTsKvEntity::getKey)
                        .thenComparing(AbstractTsKvEntity::getTs)
                );
    }

    @PreDestroy
    protected void destroy() {
        if (tsQueue != null) {
            tsQueue.destroy();
        }
    }

    @Override
    public ListenableFuture<Void> remove(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        return service.submit(() -> {
            tsKvRepository.delete(
                    entityId.getId(),
                    getOrSaveKeyId(query.getKey()),
                    query.getStartTs(),
                    query.getEndTs());
            return null;
        });
    }

    @Override
    public ListenableFuture<Integer> savePartition(TenantId tenantId, EntityId entityId, long tsKvEntryTs, String key) {
        return Futures.immediateFuture(null);
    }

    @Override
    public ListenableFuture<Void> removePartition(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        return Futures.immediateFuture(null);
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllAsync(TenantId tenantId, EntityId entityId, List<ReadTsKvQuery> queries) {
        return processFindAllAsync(tenantId, entityId, queries);
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllAsync(TenantId tenantId, EntityId entityId, ReadTsKvQuery query) {
        if (query.getAggregation() == Aggregation.NONE) {
            return findAllAsyncWithLimit(entityId, query);
        } else {
            List<ListenableFuture<Optional<TsKvEntry>>> futures = new ArrayList<>();
            long endPeriod = query.getEndTs();
            long startPeriod = query.getStartTs();
            long step = query.getInterval();
            while (startPeriod <= endPeriod) {
                long startTs = startPeriod;
                long endTs = Math.min(startPeriod + step, endPeriod + 1);
                long ts = startTs + (endTs - startTs) / 2;
                ListenableFuture<Optional<TsKvEntry>> aggregateTsKvEntry = findAndAggregateAsync(entityId, query.getKey(), startTs, endTs, ts,  query.getAggregation());
                futures.add(aggregateTsKvEntry);
                startPeriod = endTs;
            }
            return getTskvEntriesFuture(Futures.allAsList(futures));
        }
    }

    private ListenableFuture<List<TsKvEntry>> findAllAsyncWithLimit(EntityId entityId, ReadTsKvQuery query) {
        Integer keyId = getOrSaveKeyId(query.getKey());
        List<TsKvEntity> tsKvEntities = tsKvRepository.findAllWithLimit(
                entityId.getId(),
                keyId,
                query.getStartTs(),
                query.getEndTs(),
                PageRequest.of(0, query.getLimit(),
                        Sort.by(new Sort.Order(Sort.Direction.fromString(query.getOrder()),  "ts").nullsNative())));
        tsKvEntities.forEach(tsKvEntity -> tsKvEntity.setStrKey(query.getKey()));
        return Futures.immediateFuture(DaoUtil.convertDataList(tsKvEntities));
    }

    ListenableFuture<Optional<TsKvEntry>> findAndAggregateAsync(EntityId entityId, String key, long startTs, long endTs, long ts, Aggregation aggregation) {
        List<CompletableFuture<TsKvEntity>> entitiesFutures = new ArrayList<>();
        switchAggregation(entityId, key, startTs, endTs, aggregation, entitiesFutures);
        return Futures.transform(setFutures(entitiesFutures), entity -> {
            if (entity != null && entity.isNotEmpty()) {
                entity.setEntityId(entityId.getId());
                entity.setStrKey(key);
                entity.setTs(ts);
                return Optional.of(DaoUtil.getData(entity));
            } else {
                return Optional.empty();
            }
        }, MoreExecutors.directExecutor());
    }

    protected void switchAggregation(EntityId entityId, String key, long startTs, long endTs, Aggregation aggregation, List<CompletableFuture<TsKvEntity>> entitiesFutures) {
        switch (aggregation) {
            case AVG:
                findAvg(entityId, key, startTs, endTs, entitiesFutures);
                break;
            case MAX:
                findMax(entityId, key, startTs, endTs, entitiesFutures);
                break;
            case MIN:
                findMin(entityId, key, startTs, endTs, entitiesFutures);
                break;
            case SUM:
                findSum(entityId, key, startTs, endTs, entitiesFutures);
                break;
            case COUNT:
                findCount(entityId, key, startTs, endTs, entitiesFutures);
                break;
            default:
                throw new IllegalArgumentException("Not supported aggregation type: " + aggregation);
        }
    }

    protected void findCount(EntityId entityId, String key, long startTs, long endTs, List<CompletableFuture<TsKvEntity>> entitiesFutures) {
        Integer keyId = getOrSaveKeyId(key);
        entitiesFutures.add(tsKvRepository.findCount(
                entityId.getId(),
                keyId,
                startTs,
                endTs));
    }

    protected void findSum(EntityId entityId, String key, long startTs, long endTs, List<CompletableFuture<TsKvEntity>> entitiesFutures) {
        Integer keyId = getOrSaveKeyId(key);
        entitiesFutures.add(tsKvRepository.findSum(
                entityId.getId(),
                keyId,
                startTs,
                endTs));
    }

    protected void findMin(EntityId entityId, String key, long startTs, long endTs, List<CompletableFuture<TsKvEntity>> entitiesFutures) {
        Integer keyId = getOrSaveKeyId(key);
        entitiesFutures.add(tsKvRepository.findStringMin(
                entityId.getId(),
                keyId,
                startTs,
                endTs));
        entitiesFutures.add(tsKvRepository.findNumericMin(
                entityId.getId(),
                keyId,
                startTs,
                endTs));
    }

    protected void findMax(EntityId entityId, String key, long startTs, long endTs, List<CompletableFuture<TsKvEntity>> entitiesFutures) {
        Integer keyId = getOrSaveKeyId(key);
        entitiesFutures.add(tsKvRepository.findStringMax(
                entityId.getId(),
                keyId,
                startTs,
                endTs));
        entitiesFutures.add(tsKvRepository.findNumericMax(
                entityId.getId(),
                keyId,
                startTs,
                endTs));
    }

    protected void findAvg(EntityId entityId, String key, long startTs, long endTs, List<CompletableFuture<TsKvEntity>> entitiesFutures) {
        Integer keyId = getOrSaveKeyId(key);
        entitiesFutures.add(tsKvRepository.findAvg(
                entityId.getId(),
                keyId,
                startTs,
                endTs));
    }

    protected SettableFuture<TsKvEntity> setFutures(List<CompletableFuture<TsKvEntity>> entitiesFutures) {
        SettableFuture<TsKvEntity> listenableFuture = SettableFuture.create();
        CompletableFuture<List<TsKvEntity>> entities =
                CompletableFuture.allOf(entitiesFutures.toArray(new CompletableFuture[entitiesFutures.size()]))
                        .thenApply(v -> entitiesFutures.stream()
                                .map(CompletableFuture::join)
                                .collect(Collectors.toList()));

        entities.whenComplete((tsKvEntities, throwable) -> {
            if (throwable != null) {
                listenableFuture.setException(throwable);
            } else {
                TsKvEntity result = null;
                for (TsKvEntity entity : tsKvEntities) {
                    if (entity.isNotEmpty()) {
                        result = entity;
                        break;
                    }
                }
                listenableFuture.set(result);
            }
        });
        return listenableFuture;
    }
}
