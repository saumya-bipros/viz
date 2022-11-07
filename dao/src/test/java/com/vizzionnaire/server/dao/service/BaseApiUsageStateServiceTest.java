package com.vizzionnaire.server.dao.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.ApiUsageStateValue;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.id.TenantId;


public abstract class BaseApiUsageStateServiceTest extends AbstractServiceTest {

    private TenantId tenantId;

    @Before
    public void before() {
        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant");
        Tenant savedTenant = tenantService.saveTenant(tenant);
        Assert.assertNotNull(savedTenant);
        tenantId = savedTenant.getId();
    }

    @After
    public void after() {
        tenantService.deleteTenant(tenantId);
    }

    @Test
    public void testFindApiUsageStateByTenantId() {
        ApiUsageState apiUsageState = apiUsageStateService.findTenantApiUsageState(tenantId);
        Assert.assertNotNull(apiUsageState);
    }

    @Test
    public void testUpdateApiUsageState(){
        ApiUsageState apiUsageState = apiUsageStateService.findTenantApiUsageState(tenantId);
        Assert.assertNotNull(apiUsageState);
        Assert.assertTrue(apiUsageState.isTransportEnabled());
        apiUsageState.setTransportState(ApiUsageStateValue.DISABLED);
        apiUsageState = apiUsageStateService.update(apiUsageState);
        Assert.assertNotNull(apiUsageState);
        apiUsageState = apiUsageStateService.findTenantApiUsageState(tenantId);
        Assert.assertNotNull(apiUsageState);
        Assert.assertFalse(apiUsageState.isTransportEnabled());
    }

}
