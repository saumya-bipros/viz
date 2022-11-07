package com.vizzionnaire.server.dao;

import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import com.vizzionnaire.server.common.stats.StatsFactory;
import com.vizzionnaire.server.dao.JpaDaoConfig;
import com.vizzionnaire.server.dao.SqlTimeseriesDaoConfig;
import com.vizzionnaire.server.dao.SqlTsDaoConfig;
import com.vizzionnaire.server.dao.SqlTsLatestDaoConfig;
import com.vizzionnaire.server.dao.service.DaoSqlTest;

/**
 * Created by Valerii Sosliuk on 4/22/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {JpaDaoConfig.class, SqlTsDaoConfig.class, SqlTsLatestDaoConfig.class, SqlTimeseriesDaoConfig.class})
@DaoSqlTest
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class})
public abstract class AbstractJpaDaoTest {

    @MockBean(answer = Answers.RETURNS_MOCKS)
    StatsFactory statsFactory;

}
