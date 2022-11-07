package com.vizzionnaire.server.dao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vizzionnaire.server.dao.util.SqlTsOrTsLatestAnyDao;
import com.vizzionnaire.server.dao.util.TbAutoConfiguration;

@Configuration
@TbAutoConfiguration
@EnableJpaRepositories({"com.vizzionnaire.server.dao.sqlts.dictionary"})
@EntityScan({"com.vizzionnaire.server.dao.model.sqlts.dictionary"})
@EnableTransactionManagement
@SqlTsOrTsLatestAnyDao
public class SqlTimeseriesDaoConfig {

}
