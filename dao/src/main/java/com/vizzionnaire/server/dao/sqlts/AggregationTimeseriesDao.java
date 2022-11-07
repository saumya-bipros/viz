package com.vizzionnaire.server.dao.sqlts;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.kv.ReadTsKvQuery;
import com.vizzionnaire.server.common.data.kv.TsKvEntry;

import java.util.List;

public interface AggregationTimeseriesDao {

    ListenableFuture<List<TsKvEntry>> findAllAsync(TenantId tenantId, EntityId entityId, ReadTsKvQuery query);
}