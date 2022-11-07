package com.vizzionnaire.server.service.install;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.dao.util.NoSqlTsDao;

@Service
@NoSqlTsDao
@Profile("install")
public class CassandraTsDatabaseSchemaService extends CassandraAbstractDatabaseSchemaService
        implements TsDatabaseSchemaService {
    public CassandraTsDatabaseSchemaService() {
        super("schema-ts.cql");
    }
}
