package com.vizzionnaire.server.dao.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

@Slf4j
public abstract class JpaAbstractDaoListeningExecutorService {

    @Autowired
    protected JpaExecutorService service;

    @Autowired
    protected DataSource dataSource;

    protected void printWarnings(Statement statement) throws SQLException {
        SQLWarning warnings = statement.getWarnings();
        if (warnings != null) {
            log.debug("{}", warnings.getMessage());
            SQLWarning nextWarning = warnings.getNextWarning();
            while (nextWarning != null) {
                log.debug("{}", nextWarning.getMessage());
                nextWarning = nextWarning.getNextWarning();
            }
        }
    }

}
