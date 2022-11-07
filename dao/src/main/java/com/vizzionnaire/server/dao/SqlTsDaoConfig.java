package com.vizzionnaire.server.dao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vizzionnaire.server.dao.util.SqlTsDao;
import com.vizzionnaire.server.dao.util.TbAutoConfiguration;

@Configuration
@TbAutoConfiguration
@ComponentScan({"com.vizzionnaire.server.dao.sqlts.sql", "com.vizzionnaire.server.dao.sqlts.insert.sql"})
@EnableJpaRepositories({"com.vizzionnaire.server.dao.sqlts.ts", "com.vizzionnaire.server.dao.sqlts.insert.sql"})
@EntityScan({"com.vizzionnaire.server.dao.model.sqlts.ts"})
@EnableTransactionManagement
@SqlTsDao
public class SqlTsDaoConfig {

}
