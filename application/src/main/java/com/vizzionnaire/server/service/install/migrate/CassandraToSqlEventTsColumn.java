package com.vizzionnaire.server.service.install.migrate;

import com.datastax.oss.driver.api.core.cql.Row;

import static com.vizzionnaire.server.dao.model.ModelConstants.EPOCH_DIFF;

import java.util.UUID;

public class CassandraToSqlEventTsColumn extends CassandraToSqlColumn {

    CassandraToSqlEventTsColumn() {
        super("id", "ts", CassandraToSqlColumnType.BIGINT, null, false);
    }

    @Override
    public String getColumnValue(Row row) {
        UUID id = row.getUuid(getIndex());
        long ts = getTs(id);
        return ts + "";
    }

    private long getTs(UUID uuid) {
        return (uuid.timestamp() - EPOCH_DIFF) / 10000;
    }
}
