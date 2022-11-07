package com.vizzionnaire.server.dao.timeseries;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CassandraPartitionCacheKey {

    private EntityId entityId;
    private String key;
    private long partition;

}