package com.vizzionnaire.server.dao.sql.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.oauth2.OAuth2Params;
import com.vizzionnaire.server.dao.model.sql.OAuth2ParamsEntity;
import com.vizzionnaire.server.dao.oauth2.OAuth2ParamsDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaOAuth2ParamsDao extends JpaAbstractDao<OAuth2ParamsEntity, OAuth2Params> implements OAuth2ParamsDao {
    private final OAuth2ParamsRepository repository;

    @Override
    protected Class<OAuth2ParamsEntity> getEntityClass() {
        return OAuth2ParamsEntity.class;
    }

    @Override
    protected JpaRepository<OAuth2ParamsEntity, UUID> getRepository() {
        return repository;
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
