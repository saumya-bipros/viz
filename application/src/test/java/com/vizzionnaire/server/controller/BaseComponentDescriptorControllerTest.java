package com.vizzionnaire.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vizzionnaire.rule.engine.filter.TbJsFilterNode;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.plugin.ComponentDescriptor;
import com.vizzionnaire.server.common.data.plugin.ComponentScope;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.data.rule.RuleChainType;
import com.vizzionnaire.server.common.data.security.Authority;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseComponentDescriptorControllerTest extends AbstractControllerTest {

    private static final int AMOUNT_OF_DEFAULT_FILTER_NODES = 4;
    private Tenant savedTenant;
    private User tenantAdmin;

    @Before
    public void beforeTest() throws Exception {
        loginSysAdmin();

        Tenant tenant = new Tenant();
        tenant.setTitle("My tenant");
        savedTenant = doPost("/api/tenant", tenant, Tenant.class);
        Assert.assertNotNull(savedTenant);

        tenantAdmin = new User();
        tenantAdmin.setAuthority(Authority.TENANT_ADMIN);
        tenantAdmin.setTenantId(savedTenant.getId());
        tenantAdmin.setEmail("tenant2@vizzionnaire.org");
        tenantAdmin.setFirstName("Joe");
        tenantAdmin.setLastName("Downs");

        tenantAdmin = createUserAndLogin(tenantAdmin, "testPassword1");
    }

    @After
    public void afterTest() throws Exception {
        loginSysAdmin();

        doDelete("/api/tenant/" + savedTenant.getId().getId().toString())
                .andExpect(status().isOk());
    }

    @Test
    public void testGetByClazz() throws Exception {
        ComponentDescriptor descriptor =
                doGet("/api/component/" + TbJsFilterNode.class.getName(), ComponentDescriptor.class);

        Assert.assertNotNull(descriptor);
        Assert.assertNotNull(descriptor.getId());
        Assert.assertNotNull(descriptor.getName());
        Assert.assertEquals(ComponentScope.TENANT, descriptor.getScope());
        Assert.assertEquals(ComponentType.FILTER, descriptor.getType());
        Assert.assertEquals(descriptor.getClazz(), descriptor.getClazz());
    }

    @Test
    public void testGetByType() throws Exception {
        List<ComponentDescriptor> descriptors = readResponse(
                doGet("/api/components?componentTypes={componentTypes}&ruleChainType={ruleChainType}", ComponentType.FILTER, RuleChainType.CORE).andExpect(status().isOk()), new TypeReference<List<ComponentDescriptor>>() {
                });

        Assert.assertNotNull(descriptors);
        Assert.assertTrue(descriptors.size() >= AMOUNT_OF_DEFAULT_FILTER_NODES);

        for (ComponentType type : ComponentType.values()) {
            doGet("/api/components?componentTypes={componentTypes}&ruleChainType={ruleChainType}", type, RuleChainType.CORE).andExpect(status().isOk());
        }
    }

}
