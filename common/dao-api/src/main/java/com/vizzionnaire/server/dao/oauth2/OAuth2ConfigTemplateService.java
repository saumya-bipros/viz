package com.vizzionnaire.server.dao.oauth2;

import java.util.List;
import java.util.Optional;

import com.vizzionnaire.server.common.data.id.OAuth2ClientRegistrationTemplateId;
import com.vizzionnaire.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;

public interface OAuth2ConfigTemplateService {
    OAuth2ClientRegistrationTemplate saveClientRegistrationTemplate(OAuth2ClientRegistrationTemplate clientRegistrationTemplate);

    Optional<OAuth2ClientRegistrationTemplate> findClientRegistrationTemplateByProviderId(String providerId);

    OAuth2ClientRegistrationTemplate findClientRegistrationTemplateById(OAuth2ClientRegistrationTemplateId templateId);

    List<OAuth2ClientRegistrationTemplate> findAllClientRegistrationTemplates();

    void deleteClientRegistrationTemplateById(OAuth2ClientRegistrationTemplateId templateId);
}
