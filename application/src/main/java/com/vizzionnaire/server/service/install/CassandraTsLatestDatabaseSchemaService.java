package com.vizzionnaire.server.service.install;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.dao.util.NoSqlTsLatestDao;

@Service
@NoSqlTsLatestDao
@Profile("install")
public class CassandraTsLatestDatabaseSchemaService extends CassandraAbstractDatabaseSchemaService
        implements TsLatestDatabaseSchemaService {
    public CassandraTsLatestDatabaseSchemaService() {
        super("schema-ts-latest.cql");
    }
}
