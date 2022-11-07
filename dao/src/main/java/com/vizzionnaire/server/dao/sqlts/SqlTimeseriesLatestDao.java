package com.vizzionnaire.server.dao.sqlts;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.kv.Aggregation;
import com.vizzionnaire.server.common.data.kv.BaseReadTsKvQuery;
import com.vizzionnaire.server.common.data.kv.BasicTsKvEntry;
import com.vizzionnaire.server.common.data.kv.DeleteTsKvQuery;
import com.vizzionnaire.server.common.data.kv.ReadTsKvQuery;
import com.vizzionnaire.server.common.data.kv.StringDataEntry;
import com.vizzionnaire.server.common.data.kv.TsKvEntry;
import com.vizzionnaire.server.common.data.kv.TsKvLatestRemovingResult;
import com.vizzionnaire.server.common.stats.StatsFactory;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.AbstractTsKvEntity;
import com.vizzionnaire.server.dao.model.sqlts.latest.TsKvLatestCompositeKey;
import com.vizzionnaire.server.dao.model.sqlts.latest.TsKvLatestEntity;
import com.vizzionnaire.server.dao.sql.ScheduledLogExecutorComponent;
import com.vizzionnaire.server.dao.sql.TbSqlBlockingQueueParams;
import com.vizzionnaire.server.dao.sql.TbSqlBlockingQueueWrapper;
import com.vizzionnaire.server.dao.sqlts.insert.latest.InsertLatestTsRepository;
import com.vizzionnaire.server.dao.sqlts.latest.SearchTsKvLatestRepository;
import com.vizzionnaire.server.dao.sqlts.latest.TsKvLatestRepository;
import com.vizzionnaire.server.dao.timeseries.TimeseriesLatestDao;
import com.vizzionnaire.server.dao.util.SqlTsLatestAnyDao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@SqlTsLatestAnyDao
public class SqlTimeseriesLatestDao extends BaseAbstractSqlTimeseriesDao implements TimeseriesLatestDao {

    private static final String DESC_ORDER = "DESC";

    @Autowired
    private TsKvLatestRepository tsKvLatestRepository;

    @Autowired
    protected AggregationTimeseriesDao aggregationTimeseriesDao;

    @Autowired
    private SearchTsKvLatestRepository searchTsKvLatestRepository;

    @Autowired
    private InsertLatestTsRepository insertLatestTsRepository;

    private TbSqlBlockingQueueWrapper<TsKvLatestEntity> tsLatestQueue;

    @Value("${sql.ts_latest.batch_size:1000}")
    private int tsLatestBatchSize;

    @Value("${sql.ts_latest.batch_max_delay:100}")
    private long tsLatestMaxDelay;

    @Value("${sql.ts_latest.stats_print_interval_ms:1000}")
    private long tsLatestStatsPrintIntervalMs;

    @Value("${sql.ts_latest.batch_threads:4}")
    private int tsLatestBatchThreads;

    @Value("${sql.batch_sort:false}")
    protected boolean batchSortEnabled;

    @Autowired
    protected ScheduledLogExecutorComponent logExecutor;

    @Autowired
    private StatsFactory statsFactory;

    @PostConstruct
    protected void init() {
        TbSqlBlockingQueueParams tsLatestParams = TbSqlBlockingQueueParams.builder()
                .logName("TS Latest")
                .batchSize(tsLatestBatchSize)
                .maxDelay(tsLatestMaxDelay)
                .statsPrintIntervalMs(tsLatestStatsPrintIntervalMs)
                .statsNamePrefix("ts.latest")
                .batchSortEnabled(false)
                .build();

        java.util.function.Function<TsKvLatestEntity, Integer> hashcodeFunction = entity -> entity.getEntityId().hashCode();
        tsLatestQueue = new TbSqlBlockingQueueWrapper<>(tsLatestParams, hashcodeFunction, tsLatestBatchThreads, statsFactory);

        tsLatestQueue.init(logExecutor, v -> {
            Map<TsKey, TsKvLatestEntity> trueLatest = new HashMap<>();
            v.forEach(ts -> {
                TsKey key = new TsKey(ts.getEntityId(), ts.getKey());
                trueLatest.merge(key, ts, (oldTs, newTs) -> oldTs.getTs() <= newTs.getTs() ? newTs : oldTs);
            });
            List<TsKvLatestEntity> latestEntities = new ArrayList<>(trueLatest.values());
            if (batchSortEnabled) {
                latestEntities.sort(Comparator.comparing((Function<TsKvLatestEntity, UUID>) AbstractTsKvEntity::getEntityId)
                        .thenComparingInt(AbstractTsKvEntity::getKey));
            }
            insertLatestTsRepository.saveOrUpdate(latestEntities);
        }, (l, r) -> 0);
    }

    @PreDestroy
    protected void destroy() {
        if (tsLatestQueue != null) {
            tsLatestQueue.destroy();
        }
    }

    @Override
    public ListenableFuture<Void> saveLatest(TenantId tenantId, EntityId entityId, TsKvEntry tsKvEntry) {
        return getSaveLatestFuture(entityId, tsKvEntry);
    }

    @Override
    public ListenableFuture<TsKvLatestRemovingResult> removeLatest(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        return getRemoveLatestFuture(tenantId, entityId, query);
    }

    @Override
    public ListenableFuture<TsKvEntry> findLatest(TenantId tenantId, EntityId entityId, String key) {
        return getFindLatestFuture(entityId, key);
    }

    @Override
    public ListenableFuture<List<TsKvEntry>> findAllLatest(TenantId tenantId, EntityId entityId) {
        return getFindAllLatestFuture(entityId);
    }

    @Override
    public List<String> findAllKeysByDeviceProfileId(TenantId tenantId, DeviceProfileId deviceProfileId) {
        if (deviceProfileId != null) {
            return tsKvLatestRepository.getKeysByDeviceProfileId(tenantId.getId(), deviceProfileId.getId());
        } else {
            return tsKvLatestRepository.getKeysByTenantId(tenantId.getId());
        }
    }

    @Override
    public List<String> findAllKeysByEntityIds(TenantId tenantId, List<EntityId> entityIds) {
        return tsKvLatestRepository.findAllKeysByEntityIds(entityIds.stream().map(EntityId::getId).collect(Collectors.toList()));
    }

    private ListenableFuture<TsKvLatestRemovingResult> getNewLatestEntryFuture(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        ListenableFuture<List<TsKvEntry>> future = findNewLatestEntryFuture(tenantId, entityId, query);
        return Futures.transformAsync(future, entryList -> {
            if (entryList.size() == 1) {
                TsKvEntry entry = entryList.get(0);
                return Futures.transform(getSaveLatestFuture(entityId, entry), v -> new TsKvLatestRemovingResult(entry), MoreExecutors.directExecutor());
            } else {
                log.trace("Could not find new latest value for [{}], key - {}", entityId, query.getKey());
            }
            return Futures.immediateFuture(new TsKvLatestRemovingResult(query.getKey(), true));
        }, service);
    }

    private ListenableFuture<List<TsKvEntry>> findNewLatestEntryFuture(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        long startTs = 0;
        long endTs = query.getStartTs() - 1;
        ReadTsKvQuery findNewLatestQuery = new BaseReadTsKvQuery(query.getKey(), startTs, endTs, endTs - startTs, 1,
                Aggregation.NONE, DESC_ORDER);
        return aggregationTimeseriesDao.findAllAsync(tenantId, entityId, findNewLatestQuery);
    }

    protected ListenableFuture<TsKvEntry> getFindLatestFuture(EntityId entityId, String key) {
        TsKvLatestCompositeKey compositeKey =
                new TsKvLatestCompositeKey(
                        entityId.getId(),
                        getOrSaveKeyId(key));
        Optional<TsKvLatestEntity> entry = tsKvLatestRepository.findById(compositeKey);
        TsKvEntry result;
        if (entry.isPresent()) {
            TsKvLatestEntity tsKvLatestEntity = entry.get();
            tsKvLatestEntity.setStrKey(key);
            result = DaoUtil.getData(tsKvLatestEntity);
        } else {
            result = new BasicTsKvEntry(System.currentTimeMillis(), new StringDataEntry(key, null));
        }
        return Futures.immediateFuture(result);
    }

    protected ListenableFuture<TsKvLatestRemovingResult> getRemoveLatestFuture(TenantId tenantId, EntityId entityId, DeleteTsKvQuery query) {
        ListenableFuture<TsKvEntry> latestFuture = getFindLatestFuture(entityId, query.getKey());

        ListenableFuture<Boolean> booleanFuture = Futures.transform(latestFuture, tsKvEntry -> {
            long ts = tsKvEntry.getTs();
            return ts > query.getStartTs() && ts <= query.getEndTs();
        }, service);

        ListenableFuture<Boolean> removedLatestFuture = Futures.transformAsync(booleanFuture, isRemove -> {
            if (isRemove) {
                TsKvLatestEntity latestEntity = new TsKvLatestEntity();
                latestEntity.setEntityId(entityId.getId());
                latestEntity.setKey(getOrSaveKeyId(query.getKey()));
                return service.submit(() -> {
                    tsKvLatestRepository.delete(latestEntity);
                    return true;
                });
            }
            return Futures.immediateFuture(false);
        }, service);

        return Futures.transformAsync(removedLatestFuture, isRemoved -> {
            if (isRemoved && query.getRewriteLatestIfDeleted()) {
                return getNewLatestEntryFuture(tenantId, entityId, query);
            }
            return Futures.immediateFuture(new TsKvLatestRemovingResult(query.getKey(), isRemoved));
        }, MoreExecutors.directExecutor());
    }

    protected ListenableFuture<List<TsKvEntry>> getFindAllLatestFuture(EntityId entityId) {
        return Futures.immediateFuture(
                DaoUtil.convertDataList(Lists.newArrayList(
                        searchTsKvLatestRepository.findAllByEntityId(entityId.getId()))));
    }

    protected ListenableFuture<Void> getSaveLatestFuture(EntityId entityId, TsKvEntry tsKvEntry) {
        TsKvLatestEntity latestEntity = new TsKvLatestEntity();
        latestEntity.setEntityId(entityId.getId());
        latestEntity.setTs(tsKvEntry.getTs());
        latestEntity.setKey(getOrSaveKeyId(tsKvEntry.getKey()));
        latestEntity.setStrValue(tsKvEntry.getStrValue().orElse(null));
        latestEntity.setDoubleValue(tsKvEntry.getDoubleValue().orElse(null));
        latestEntity.setLongValue(tsKvEntry.getLongValue().orElse(null));
        latestEntity.setBooleanValue(tsKvEntry.getBooleanValue().orElse(null));
        latestEntity.setJsonValue(tsKvEntry.getJsonValue().orElse(null));

        return tsLatestQueue.add(latestEntity);
    }

}
