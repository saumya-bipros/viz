package com.vizzionnaire.server.dao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vizzionnaire.server.dao.util.TbAutoConfiguration;
import com.vizzionnaire.server.dao.util.TimescaleDBTsLatestDao;

@Configuration
@TbAutoConfiguration
@ComponentScan({"com.vizzionnaire.server.dao.sqlts.timescale"})
@EnableJpaRepositories({"com.vizzionnaire.server.dao.sqlts.insert.latest.sql", "com.vizzionnaire.server.dao.sqlts.latest"})
@EntityScan({"com.vizzionnaire.server.dao.model.sqlts.latest"})
@EnableTransactionManagement
@TimescaleDBTsLatestDao
public class TimescaleTsLatestDaoConfig {

}
