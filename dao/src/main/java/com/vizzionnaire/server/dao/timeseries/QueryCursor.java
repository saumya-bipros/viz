package com.vizzionnaire.server.dao.timeseries;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

import com.vizzionnaire.server.common.data.kv.TsKvQuery;

public class QueryCursor {

    @Getter
    protected final String entityType;
    @Getter
    protected final UUID entityId;
    @Getter
    protected final String key;
    @Getter
    private final long startTs;
    @Getter
    private final long endTs;

    final List<Long> partitions;
    private int partitionIndex;

    public QueryCursor(String entityType, UUID entityId, TsKvQuery baseQuery, List<Long> partitions) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.key = baseQuery.getKey();
        this.startTs = baseQuery.getStartTs();
        this.endTs = baseQuery.getEndTs();
        this.partitions = partitions;
        this.partitionIndex = partitions.size() - 1;
    }

    public boolean hasNextPartition() {
        return partitionIndex >= 0;
    }

    public long getNextPartition() {
        long partition = partitions.get(partitionIndex);
        partitionIndex--;
        return partition;
    }

}
