package com.vizzionnaire.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.security.Authority;
import com.vizzionnaire.server.common.data.widget.WidgetType;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseWidgetTypeControllerTest extends AbstractControllerTest {

    private IdComparator<WidgetType> idComparator = new IdComparator<>();

    private Tenant savedTenant;
    private WidgetsBundle savedWidgetsBundle;
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

        WidgetsBundle widgetsBundle = new WidgetsBundle();
        widgetsBundle.setTitle("My widgets bundle");
        savedWidgetsBundle = doPost("/api/widgetsBundle", widgetsBundle, WidgetsBundle.class);
    }

    @After
    public void afterTest() throws Exception {
        loginSysAdmin();

        doDelete("/api/tenant/"+savedTenant.getId().getId().toString())
                .andExpect(status().isOk());
    }

    @Test
    public void testSaveWidgetType() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        WidgetTypeDetails savedWidgetType = doPost("/api/widgetType", widgetType, WidgetTypeDetails.class);

        Assert.assertNotNull(savedWidgetType);
        Assert.assertNotNull(savedWidgetType.getId());
        Assert.assertNotNull(savedWidgetType.getAlias());
        Assert.assertTrue(savedWidgetType.getCreatedTime() > 0);
        Assert.assertEquals(savedTenant.getId(), savedWidgetType.getTenantId());
        Assert.assertEquals(widgetType.getName(), savedWidgetType.getName());
        Assert.assertEquals(widgetType.getDescriptor(), savedWidgetType.getDescriptor());
        Assert.assertEquals(savedWidgetsBundle.getAlias(), savedWidgetType.getBundleAlias());

        savedWidgetType.setName("New Widget Type");

        doPost("/api/widgetType", savedWidgetType, WidgetType.class);

        WidgetTypeDetails foundWidgetType = doGet("/api/widgetType/" + savedWidgetType.getId().getId().toString(), WidgetTypeDetails.class);
        Assert.assertEquals(foundWidgetType.getName(), savedWidgetType.getName());
    }

    @Test
    public void testUpdateWidgetTypeFromDifferentTenant() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        WidgetTypeDetails savedWidgetType = doPost("/api/widgetType", widgetType, WidgetTypeDetails.class);

        loginDifferentTenant();
        doPost("/api/widgetType", savedWidgetType, WidgetTypeDetails.class, status().isForbidden());
        deleteDifferentTenant();
    }

    @Test
    public void testFindWidgetTypeById() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        WidgetTypeDetails savedWidgetType = doPost("/api/widgetType", widgetType, WidgetTypeDetails.class);
        WidgetTypeDetails foundWidgetType = doGet("/api/widgetType/" + savedWidgetType.getId().getId().toString(), WidgetTypeDetails.class);
        Assert.assertNotNull(foundWidgetType);
        Assert.assertEquals(savedWidgetType, foundWidgetType);
    }

    @Test
    public void testDeleteWidgetType() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        WidgetTypeDetails savedWidgetType = doPost("/api/widgetType", widgetType, WidgetTypeDetails.class);

        doDelete("/api/widgetType/"+savedWidgetType.getId().getId().toString())
                .andExpect(status().isOk());

        doGet("/api/widgetType/"+savedWidgetType.getId().getId().toString())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testSaveWidgetTypeWithEmptyName() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        doPost("/api/widgetType", widgetType)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Widgets type name should be specified")));
    }

    @Test
    public void testSaveWidgetTypeWithEmptyBundleAlias() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        doPost("/api/widgetType", widgetType)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Widgets type bundle alias should be specified")));
    }

    @Test
    public void testSaveWidgetTypeWithEmptyDescriptor() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{}", JsonNode.class));
        doPost("/api/widgetType", widgetType)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Widgets type descriptor can't be empty")));
    }

    @Test
    public void testSaveWidgetTypeWithInvalidBundleAlias() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias("some_alias");
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        doPost("/api/widgetType", widgetType)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Widget type is referencing to non-existent widgets bundle")));
    }

    @Test
    public void testUpdateWidgetTypeBundleAlias() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        WidgetTypeDetails savedWidgetType = doPost("/api/widgetType", widgetType, WidgetTypeDetails.class);
        savedWidgetType.setBundleAlias("some_alias");
        doPost("/api/widgetType", savedWidgetType)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Update of widget type bundle alias is prohibited")));

    }

    @Test
    public void testUpdateWidgetTypeAlias() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        WidgetTypeDetails savedWidgetType = doPost("/api/widgetType", widgetType, WidgetTypeDetails.class);
        savedWidgetType.setAlias("some_alias");
        doPost("/api/widgetType", savedWidgetType)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString("Update of widget type alias is prohibited")));

    }

    @Test
    public void testGetBundleWidgetTypes() throws Exception {
        List<WidgetType> widgetTypes = new ArrayList<>();
        for (int i=0;i<89;i++) {
            WidgetTypeDetails widgetType = new WidgetTypeDetails();
            widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
            widgetType.setName("Widget Type " + i);
            widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
            widgetTypes.add(new WidgetType(doPost("/api/widgetType", widgetType, WidgetTypeDetails.class)));
        }

        List<WidgetType> loadedWidgetTypes = doGetTyped("/api/widgetTypes?isSystem={isSystem}&bundleAlias={bundleAlias}",
                new TypeReference<>(){}, false, savedWidgetsBundle.getAlias());

        Collections.sort(widgetTypes, idComparator);
        Collections.sort(loadedWidgetTypes, idComparator);

        Assert.assertEquals(widgetTypes, loadedWidgetTypes);
    }

    @Test
    public void testGetWidgetType() throws Exception {
        WidgetTypeDetails widgetType = new WidgetTypeDetails();
        widgetType.setBundleAlias(savedWidgetsBundle.getAlias());
        widgetType.setName("Widget Type");
        widgetType.setDescriptor(new ObjectMapper().readValue("{ \"someKey\": \"someValue\" }", JsonNode.class));
        WidgetTypeDetails savedWidgetType = doPost("/api/widgetType", widgetType, WidgetTypeDetails.class);
        WidgetType foundWidgetType = doGet("/api/widgetType?isSystem={isSystem}&bundleAlias={bundleAlias}&alias={alias}",
                WidgetType.class, false, savedWidgetsBundle.getAlias(), savedWidgetType.getAlias());
        Assert.assertNotNull(foundWidgetType);
        Assert.assertEquals(new WidgetType(savedWidgetType), foundWidgetType);
    }

}
