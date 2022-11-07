package com.vizzionnaire.server.dao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.vizzionnaire.server.dao.util.TbAutoConfiguration;

/**
 * @author Valerii Sosliuk
 */
@Configuration
@TbAutoConfiguration
@ComponentScan({"com.vizzionnaire.server.dao.sql", "com.vizzionnaire.server.dao.attributes", "com.vizzionnaire.server.dao.cache", "com.vizzionnaire.server.cache"})
@EnableJpaRepositories("com.vizzionnaire.server.dao.sql")
@EntityScan("com.vizzionnaire.server.dao.model.sql")
@EnableTransactionManagement
public class JpaDaoConfig {

}
