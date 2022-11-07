package com.vizzionnaire.server.dao.sql.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vizzionnaire.server.dao.model.sql.OAuth2ClientRegistrationTemplateEntity;

import java.util.UUID;

public interface OAuth2ClientRegistrationTemplateRepository extends JpaRepository<OAuth2ClientRegistrationTemplateEntity, UUID> {

    OAuth2ClientRegistrationTemplateEntity findByProviderId(String providerId);

}
