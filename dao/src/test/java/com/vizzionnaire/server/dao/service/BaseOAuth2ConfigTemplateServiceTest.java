package com.vizzionnaire.server.dao.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.oauth2.MapperType;
import com.vizzionnaire.server.common.data.oauth2.OAuth2BasicMapperConfig;
import com.vizzionnaire.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;
import com.vizzionnaire.server.common.data.oauth2.OAuth2MapperConfig;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.oauth2.OAuth2ConfigTemplateService;

import java.util.Arrays;
import java.util.UUID;

public abstract class BaseOAuth2ConfigTemplateServiceTest extends AbstractServiceTest {

    @Autowired
    protected OAuth2ConfigTemplateService oAuth2ConfigTemplateService;

    @Before
    public void beforeRun() throws Exception {
        Assert.assertTrue(oAuth2ConfigTemplateService.findAllClientRegistrationTemplates().isEmpty());
    }

    @After
    public void after() throws Exception {
        oAuth2ConfigTemplateService.findAllClientRegistrationTemplates().forEach(clientRegistrationTemplate -> {
            oAuth2ConfigTemplateService.deleteClientRegistrationTemplateById(clientRegistrationTemplate.getId());
        });

        Assert.assertTrue(oAuth2ConfigTemplateService.findAllClientRegistrationTemplates().isEmpty());
    }


    @Test(expected = DataValidationException.class)
    public void testSaveDuplicateProviderId() {
        OAuth2ClientRegistrationTemplate first = validClientRegistrationTemplate("providerId");
        OAuth2ClientRegistrationTemplate second = validClientRegistrationTemplate("providerId");
        oAuth2ConfigTemplateService.saveClientRegistrationTemplate(first);
        oAuth2ConfigTemplateService.saveClientRegistrationTemplate(second);
    }

    @Test
    public void testCreateNewTemplate() {
        OAuth2ClientRegistrationTemplate clientRegistrationTemplate = validClientRegistrationTemplate(UUID.randomUUID().toString());
        OAuth2ClientRegistrationTemplate savedClientRegistrationTemplate = oAuth2ConfigTemplateService.saveClientRegistrationTemplate(clientRegistrationTemplate);

        Assert.assertNotNull(savedClientRegistrationTemplate);
        Assert.assertNotNull(savedClientRegistrationTemplate.getId());
        clientRegistrationTemplate.setId(savedClientRegistrationTemplate.getId());
        clientRegistrationTemplate.setCreatedTime(savedClientRegistrationTemplate.getCreatedTime());
        Assert.assertEquals(clientRegistrationTemplate, savedClientRegistrationTemplate);
    }

    @Test
    public void testFindTemplate() {
        OAuth2ClientRegistrationTemplate clientRegistrationTemplate = validClientRegistrationTemplate(UUID.randomUUID().toString());
        OAuth2ClientRegistrationTemplate savedClientRegistrationTemplate = oAuth2ConfigTemplateService.saveClientRegistrationTemplate(clientRegistrationTemplate);

        OAuth2ClientRegistrationTemplate foundClientRegistrationTemplate = oAuth2ConfigTemplateService.findClientRegistrationTemplateById(savedClientRegistrationTemplate.getId());
        Assert.assertEquals(savedClientRegistrationTemplate, foundClientRegistrationTemplate);
    }

    @Test
    public void testFindAll() {
        oAuth2ConfigTemplateService.saveClientRegistrationTemplate(validClientRegistrationTemplate(UUID.randomUUID().toString()));
        oAuth2ConfigTemplateService.saveClientRegistrationTemplate(validClientRegistrationTemplate(UUID.randomUUID().toString()));

        Assert.assertEquals(2, oAuth2ConfigTemplateService.findAllClientRegistrationTemplates().size());
    }

    @Test
    public void testDeleteTemplate() {
        oAuth2ConfigTemplateService.saveClientRegistrationTemplate(validClientRegistrationTemplate(UUID.randomUUID().toString()));
        oAuth2ConfigTemplateService.saveClientRegistrationTemplate(validClientRegistrationTemplate(UUID.randomUUID().toString()));
        OAuth2ClientRegistrationTemplate saved = oAuth2ConfigTemplateService.saveClientRegistrationTemplate(validClientRegistrationTemplate(UUID.randomUUID().toString()));

        Assert.assertEquals(3, oAuth2ConfigTemplateService.findAllClientRegistrationTemplates().size());
        Assert.assertNotNull(oAuth2ConfigTemplateService.findClientRegistrationTemplateById(saved.getId()));

        oAuth2ConfigTemplateService.deleteClientRegistrationTemplateById(saved.getId());

        Assert.assertEquals(2, oAuth2ConfigTemplateService.findAllClientRegistrationTemplates().size());
        Assert.assertNull(oAuth2ConfigTemplateService.findClientRegistrationTemplateById(saved.getId()));
    }

    private OAuth2ClientRegistrationTemplate validClientRegistrationTemplate(String providerId) {
        OAuth2ClientRegistrationTemplate clientRegistrationTemplate = new OAuth2ClientRegistrationTemplate();
        clientRegistrationTemplate.setProviderId(providerId);
        clientRegistrationTemplate.setAdditionalInfo(mapper.createObjectNode().put(UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        clientRegistrationTemplate.setMapperConfig(OAuth2MapperConfig.builder()
                .type(MapperType.BASIC)
                .basic(OAuth2BasicMapperConfig.builder()
                        .firstNameAttributeKey("firstName")
                        .lastNameAttributeKey("lastName")
                        .emailAttributeKey("email")
                        .tenantNamePattern("tenant")
                        .defaultDashboardName("Test")
                        .alwaysFullScreen(true)
                        .build()
                )
                .build());
        clientRegistrationTemplate.setAuthorizationUri("authorizationUri");
        clientRegistrationTemplate.setAccessTokenUri("tokenUri");
        clientRegistrationTemplate.setScope(Arrays.asList("scope1", "scope2"));
        clientRegistrationTemplate.setUserInfoUri("userInfoUri");
        clientRegistrationTemplate.setUserNameAttributeName("userNameAttributeName");
        clientRegistrationTemplate.setJwkSetUri("jwkSetUri");
        clientRegistrationTemplate.setClientAuthenticationMethod("clientAuthenticationMethod");
        clientRegistrationTemplate.setComment("comment");
        clientRegistrationTemplate.setLoginButtonIcon("icon");
        clientRegistrationTemplate.setLoginButtonLabel("label");
        clientRegistrationTemplate.setHelpLink("helpLink");
        return clientRegistrationTemplate;
    }
}
