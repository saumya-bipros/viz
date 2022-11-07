package com.vizzionnaire.server.dao.sql.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.oauth2.OAuth2ClientRegistrationTemplate;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.OAuth2ClientRegistrationTemplateEntity;
import com.vizzionnaire.server.dao.oauth2.OAuth2ClientRegistrationTemplateDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaOAuth2ClientRegistrationTemplateDao extends JpaAbstractDao<OAuth2ClientRegistrationTemplateEntity, OAuth2ClientRegistrationTemplate> implements OAuth2ClientRegistrationTemplateDao {
    private final OAuth2ClientRegistrationTemplateRepository repository;

    @Override
    protected Class<OAuth2ClientRegistrationTemplateEntity> getEntityClass() {
        return OAuth2ClientRegistrationTemplateEntity.class;
    }

    @Override
    protected JpaRepository<OAuth2ClientRegistrationTemplateEntity, UUID> getRepository() {
        return repository;
    }

    @Override
    public Optional<OAuth2ClientRegistrationTemplate> findByProviderId(String providerId) {
        OAuth2ClientRegistrationTemplate oAuth2ClientRegistrationTemplate = DaoUtil.getData(repository.findByProviderId(providerId));
        return Optional.ofNullable(oAuth2ClientRegistrationTemplate);
    }

    @Override
    public List<OAuth2ClientRegistrationTemplate> findAll() {
        Iterable<OAuth2ClientRegistrationTemplateEntity> entities = repository.findAll();
        List<OAuth2ClientRegistrationTemplate> result = new ArrayList<>();
        entities.forEach(entity -> result.add(DaoUtil.getData(entity)));
        return result;
    }
}
