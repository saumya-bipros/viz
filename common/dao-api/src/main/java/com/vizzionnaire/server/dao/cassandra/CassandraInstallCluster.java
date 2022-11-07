package com.vizzionnaire.server.dao.cassandra;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.dao.util.NoSqlAnyDao;

import javax.annotation.PostConstruct;

@Component("CassandraInstallCluster")
@NoSqlAnyDao
@Profile("install")
public class CassandraInstallCluster extends AbstractCassandraCluster {

    @PostConstruct
    public void init() {
        super.init(null);
    }

}
