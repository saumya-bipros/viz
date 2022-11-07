package com.vizzionnaire.server.dao.oauth2;

import com.vizzionnaire.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;
import com.vizzionnaire.server.dao.Dao;

import java.util.List;
import java.util.Optional;

public interface OAuth2ClientRegistrationTemplateDao extends Dao<OAuth2ClientRegistrationTemplate> {

    Optional<OAuth2ClientRegistrationTemplate> findByProviderId(String providerId);

    List<OAuth2ClientRegistrationTemplate> findAll();
}
