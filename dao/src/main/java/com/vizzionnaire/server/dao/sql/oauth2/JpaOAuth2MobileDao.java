package com.vizzionnaire.server.dao.sql.oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.oauth2.OAuth2Mobile;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.OAuth2MobileEntity;
import com.vizzionnaire.server.dao.oauth2.OAuth2MobileDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaOAuth2MobileDao extends JpaAbstractDao<OAuth2MobileEntity, OAuth2Mobile> implements OAuth2MobileDao {

    private final OAuth2MobileRepository repository;

    @Override
    protected Class<OAuth2MobileEntity> getEntityClass() {
        return OAuth2MobileEntity.class;
    }

    @Override
    protected JpaRepository<OAuth2MobileEntity, UUID> getRepository() {
        return repository;
    }

    @Override
    public List<OAuth2Mobile> findByOAuth2ParamsId(UUID oauth2ParamsId) {
        return DaoUtil.convertDataList(repository.findByOauth2ParamsId(oauth2ParamsId));
    }

}

