package com.vizzionnaire.server.dao.sqlts.insert.sql;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.vizzionnaire.server.dao.timeseries.SqlPartition;
import com.vizzionnaire.server.dao.util.SqlTsDao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class SqlPartitioningRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(SqlPartition partition) {
        entityManager.createNativeQuery(partition.getQuery()).executeUpdate();
    }

}
