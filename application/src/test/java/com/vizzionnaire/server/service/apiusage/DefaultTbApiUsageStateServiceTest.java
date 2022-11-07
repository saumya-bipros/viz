package com.vizzionnaire.server.service.apiusage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.vizzionnaire.rule.engine.api.MailService;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.queue.ServiceType;
import com.vizzionnaire.server.common.msg.queue.TopicPartitionInfo;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.timeseries.TimeseriesService;
import com.vizzionnaire.server.dao.usagerecord.ApiUsageStateService;
import com.vizzionnaire.server.queue.discovery.PartitionService;
import com.vizzionnaire.server.queue.scheduler.SchedulerComponent;
import com.vizzionnaire.server.service.apiusage.DefaultTbApiUsageStateService;
import com.vizzionnaire.server.service.apiusage.TenantApiUsageState;
import com.vizzionnaire.server.service.executors.DbCallbackExecutorService;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class DefaultTbApiUsageStateServiceTest {

    @Mock
    TenantService tenantService;
    @Mock
    TimeseriesService tsService;
    @Mock
    TbClusterService clusterService;
    @Mock
    PartitionService partitionService;
    @Mock
    TenantApiUsageState tenantUsageStateMock;
    @Mock
    ApiUsageStateService apiUsageStateService;
    @Mock
    TbTenantProfileCache tenantProfileCache;
    @Mock
    MailService mailService;
    @Mock
    DbCallbackExecutorService dbExecutor;

    TenantId tenantId = TenantId.fromUUID(UUID.fromString("00797a3b-7aeb-4b5b-b57a-c2a810d0f112"));

    DefaultTbApiUsageStateService service;

    @Before
    public void setUp() {
        service = spy(new DefaultTbApiUsageStateService(clusterService, partitionService, tenantService, tsService, apiUsageStateService, tenantProfileCache, mailService, dbExecutor));
    }

    @Test
    public void givenTenantIdFromEntityStatesMap_whenGetApiUsageState() {
        service.myUsageStates.put(tenantId, tenantUsageStateMock);
        ApiUsageState tenantUsageState = service.getApiUsageState(tenantId);
        assertThat(tenantUsageState, is(tenantUsageStateMock.getApiUsageState()));
        Mockito.verify(service, never()).getOrFetchState(tenantId, tenantId);
    }

}