package com.vizzionnaire.server.service.stats;

import com.google.common.util.concurrent.FutureCallback;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.kv.BasicTsKvEntry;
import com.vizzionnaire.server.common.data.kv.JsonDataEntry;
import com.vizzionnaire.server.common.data.kv.LongDataEntry;
import com.vizzionnaire.server.common.data.kv.TsKvEntry;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.queue.discovery.TbServiceInfoProvider;
import com.vizzionnaire.server.queue.util.TbRuleEngineComponent;
import com.vizzionnaire.server.service.queue.TbRuleEngineConsumerStats;
import com.vizzionnaire.server.service.telemetry.TelemetrySubscriptionService;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@TbRuleEngineComponent
@Service
@Slf4j
public class DefaultRuleEngineStatisticsService implements RuleEngineStatisticsService {

    public static final String TB_SERVICE_QUEUE = "TbServiceQueue";
    public static final FutureCallback<Integer> CALLBACK = new FutureCallback<Integer>() {
        @Override
        public void onSuccess(@Nullable Integer result) {

        }

        @Override
        public void onFailure(Throwable t) {
            log.warn("Failed to persist statistics", t);
        }
    };

    private final TbServiceInfoProvider serviceInfoProvider;
    private final TelemetrySubscriptionService tsService;
    private final Lock lock = new ReentrantLock();
    private final AssetService assetService;
    private final ConcurrentMap<TenantQueueKey, AssetId> tenantQueueAssets;

    public DefaultRuleEngineStatisticsService(TelemetrySubscriptionService tsService, TbServiceInfoProvider serviceInfoProvider, AssetService assetService) {
        this.tsService = tsService;
        this.serviceInfoProvider = serviceInfoProvider;
        this.assetService = assetService;
        this.tenantQueueAssets = new ConcurrentHashMap<>();
    }

    @Override
    public void reportQueueStats(long ts, TbRuleEngineConsumerStats ruleEngineStats) {
        String queueName = ruleEngineStats.getQueueName();
        ruleEngineStats.getTenantStats().forEach((id, stats) -> {
            TenantId tenantId = TenantId.fromUUID(id);
            try {
                AssetId serviceAssetId = getServiceAssetId(tenantId, queueName);
                if (stats.getTotalMsgCounter().get() > 0) {
                    List<TsKvEntry> tsList = stats.getCounters().entrySet().stream()
                            .map(kv -> new BasicTsKvEntry(ts, new LongDataEntry(kv.getKey(), (long) kv.getValue().get())))
                            .collect(Collectors.toList());
                    if (!tsList.isEmpty()) {
                        tsService.saveAndNotifyInternal(tenantId, serviceAssetId, tsList, CALLBACK);
                    }
                }
            } catch (DataValidationException e) {
                if (!e.getMessage().equalsIgnoreCase("Asset is referencing to non-existent tenant!")) {
                    throw e;
                }
            }
        });
        ruleEngineStats.getTenantExceptions().forEach((tenantId, e) -> {
            TsKvEntry tsKv = new BasicTsKvEntry(e.getTs(), new JsonDataEntry("ruleEngineException", e.toJsonString()));
            try {
                tsService.saveAndNotifyInternal(tenantId, getServiceAssetId(tenantId, queueName), Collections.singletonList(tsKv), CALLBACK);
            } catch (DataValidationException e2) {
                if (!e2.getMessage().equalsIgnoreCase("Asset is referencing to non-existent tenant!")) {
                    throw e2;
                }
            }
        });
    }

    private AssetId getServiceAssetId(TenantId tenantId, String queueName) {
        TenantQueueKey key = new TenantQueueKey(tenantId, queueName);
        AssetId assetId = tenantQueueAssets.get(key);
        if (assetId == null) {
            lock.lock();
            try {
                assetId = tenantQueueAssets.get(key);
                if (assetId == null) {
                    Asset asset = assetService.findAssetByTenantIdAndName(tenantId, queueName + "_" + serviceInfoProvider.getServiceId());
                    if (asset == null) {
                        asset = new Asset();
                        asset.setTenantId(tenantId);
                        asset.setName(queueName + "_" + serviceInfoProvider.getServiceId());
                        asset.setType(TB_SERVICE_QUEUE);
                        asset = assetService.saveAsset(asset);
                    }
                    assetId = asset.getId();
                    tenantQueueAssets.put(key, assetId);
                }
            } finally {
                lock.unlock();
            }
        }
        return assetId;
    }

    @Data
    private static class TenantQueueKey {
        private final TenantId tenantId;
        private final String queueName;
    }
}
