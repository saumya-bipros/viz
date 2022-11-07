package com.vizzionnaire.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainType;
import com.vizzionnaire.server.common.data.security.Authority;
import com.vizzionnaire.server.dao.exception.DataValidationException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public abstract class BaseRuleChainControllerTest extends AbstractControllerTest {

    private IdComparator<RuleChain> idComparator = new IdComparator<>();

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
    public void testSaveRuleChain() throws Exception {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setName("RuleChain");

        Mockito.reset(tbClusterService, auditLogService);

        RuleChain savedRuleChain = doPost("/api/ruleChain", ruleChain, RuleChain.class);
        Assert.assertNotNull(savedRuleChain);
        Assert.assertNotNull(savedRuleChain.getId());
        Assert.assertTrue(savedRuleChain.getCreatedTime() > 0);
        Assert.assertEquals(ruleChain.getName(), savedRuleChain.getName());

        testNotifyEntityOneTimeMsgToEdgeServiceNever(savedRuleChain, savedRuleChain.getId(), savedRuleChain.getId(),
                savedTenant.getId(), tenantAdmin.getCustomerId(), tenantAdmin.getId(), tenantAdmin.getEmail(),
                ActionType.ADDED);

        savedRuleChain.setName("New RuleChain");
        doPost("/api/ruleChain", savedRuleChain, RuleChain.class);
        RuleChain foundRuleChain = doGet("/api/ruleChain/" + savedRuleChain.getId().getId().toString(), RuleChain.class);
        Assert.assertEquals(savedRuleChain.getName(), foundRuleChain.getName());

        testNotifyEntityOneTimeMsgToEdgeServiceNever(savedRuleChain, savedRuleChain.getId(), savedRuleChain.getId(),
                savedTenant.getId(), tenantAdmin.getCustomerId(), tenantAdmin.getId(), tenantAdmin.getEmail(),
                ActionType.UPDATED);
    }

    @Test
    public void testSaveRuleChainWithViolationOfLengthValidation() throws Exception {

        Mockito.reset(tbClusterService, auditLogService);

        RuleChain ruleChain = new RuleChain();
        ruleChain.setName(StringUtils.randomAlphabetic(300));
        String msgError = msgErrorFieldLength("name");
        doPost("/api/ruleChain", ruleChain)
                .andExpect(status().isBadRequest())
                .andExpect(statusReason(containsString(msgError)));

        ruleChain.setTenantId(savedTenant.getId());
        testNotifyEntityEqualsOneTimeServiceNeverError(ruleChain,
                savedTenant.getId(), tenantAdmin.getId(), tenantAdmin.getEmail(),
                ActionType.ADDED, new DataValidationException(msgError));
    }

    @Test
    public void testFindRuleChainById() throws Exception {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setName("RuleChain");
        RuleChain savedRuleChain = doPost("/api/ruleChain", ruleChain, RuleChain.class);
        RuleChain foundRuleChain = doGet("/api/ruleChain/" + savedRuleChain.getId().getId().toString(), RuleChain.class);
        Assert.assertNotNull(foundRuleChain);
        Assert.assertEquals(savedRuleChain, foundRuleChain);
    }

    @Test
    public void testDeleteRuleChain() throws Exception {
        RuleChain ruleChain = new RuleChain();
        ruleChain.setName("RuleChain");
        RuleChain savedRuleChain = doPost("/api/ruleChain", ruleChain, RuleChain.class);

        Mockito.reset(tbClusterService, auditLogService);

        String entityIdStr = savedRuleChain.getId().getId().toString();
        doDelete("/api/ruleChain/" + savedRuleChain.getId().getId().toString())
                .andExpect(status().isOk());

        testNotifyEntityBroadcastEntityStateChangeEventOneTimeMsgToEdgeServiceNever(savedRuleChain, savedRuleChain.getId(), savedRuleChain.getId(),
                savedTenant.getId(), tenantAdmin.getCustomerId(), tenantAdmin.getId(), tenantAdmin.getEmail(),
                ActionType.DELETED, savedRuleChain.getId().getId().toString());

        doGet("/api/ruleChain/" + entityIdStr)
                .andExpect(status().isNotFound())
                .andExpect(statusReason(containsString(msgErrorNoFound("Rule chain", entityIdStr))));
    }

    @Test
    public void testFindEdgeRuleChainsByTenantIdAndName() throws Exception {
        Edge edge = constructEdge("My edge", "default");
        Edge savedEdge = doPost("/api/edge", edge, Edge.class);


        List<RuleChain> edgeRuleChains = new ArrayList<>();
        PageLink pageLink = new PageLink(17);
        PageData<RuleChain> pageData = doGetTypedWithPageLink("/api/edge/" + savedEdge.getId().getId() + "/ruleChains?",
                new TypeReference<>() {
                }, pageLink);
        Assert.assertFalse(pageData.hasNext());
        Assert.assertEquals(1, pageData.getTotalElements());
        edgeRuleChains.addAll(pageData.getData());

        Mockito.reset(tbClusterService, auditLogService);

        int cntEntity = 28;
        for (int i = 0; i < cntEntity; i++) {
            RuleChain ruleChain = new RuleChain();
            ruleChain.setName("RuleChain " + i);
            ruleChain.setType(RuleChainType.EDGE);
            RuleChain savedRuleChain = doPost("/api/ruleChain", ruleChain, RuleChain.class);
            doPost("/api/edge/" + savedEdge.getId().getId().toString()
                    + "/ruleChain/" + savedRuleChain.getId().getId().toString(), RuleChain.class);
            edgeRuleChains.add(savedRuleChain);
        }

        testNotifyManyEntityManyTimeMsgToEdgeServiceEntityEqAny(new RuleChain(), new RuleChain(),
                savedTenant.getId(), tenantAdmin.getCustomerId(), tenantAdmin.getId(), tenantAdmin.getEmail(),
                ActionType.ADDED, ActionType.ADDED, cntEntity, 0, cntEntity * 2);
        testNotifyManyEntityManyTimeMsgToEdgeServiceEntityEqAny(new RuleChain(), new RuleChain(),
                savedTenant.getId(), tenantAdmin.getCustomerId(), tenantAdmin.getId(), tenantAdmin.getEmail(),
                ActionType.ASSIGNED_TO_EDGE, ActionType.ASSIGNED_TO_EDGE, cntEntity, cntEntity, cntEntity * 2,
                new String(), new String(), new String());
        Mockito.reset(tbClusterService, auditLogService);

        List<RuleChain> loadedEdgeRuleChains = new ArrayList<>();
        pageLink = new PageLink(17);
        do {
            pageData = doGetTypedWithPageLink("/api/edge/" + savedEdge.getId().getId() + "/ruleChains?",
                    new TypeReference<>() {
                    }, pageLink);
            loadedEdgeRuleChains.addAll(pageData.getData());
            if (pageData.hasNext()) {
                pageLink = pageLink.nextPageLink();
            }
        } while (pageData.hasNext());

        Collections.sort(edgeRuleChains, idComparator);
        Collections.sort(loadedEdgeRuleChains, idComparator);

        Assert.assertEquals(edgeRuleChains, loadedEdgeRuleChains);

        for (RuleChain ruleChain : loadedEdgeRuleChains) {
            if (!ruleChain.isRoot()) {
                doDelete("/api/edge/" + savedEdge.getId().getId().toString()
                        + "/ruleChain/" + ruleChain.getId().getId().toString(), RuleChain.class);
            }
        }

        testNotifyManyEntityManyTimeMsgToEdgeServiceEntityEqAnyAdditionalInfoAny(new RuleChain(), new RuleChain(),
                savedTenant.getId(), tenantAdmin.getCustomerId(), tenantAdmin.getId(), tenantAdmin.getEmail(),
                ActionType.UNASSIGNED_FROM_EDGE, ActionType.UNASSIGNED_FROM_EDGE, cntEntity, cntEntity, 3);

        pageLink = new PageLink(17);
        pageData = doGetTypedWithPageLink("/api/edge/" + savedEdge.getId().getId() + "/ruleChains?",
                new TypeReference<>() {
                }, pageLink);
        Assert.assertFalse(pageData.hasNext());
        Assert.assertEquals(1, pageData.getTotalElements());
    }

}
