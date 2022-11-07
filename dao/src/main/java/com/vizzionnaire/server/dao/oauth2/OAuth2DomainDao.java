package com.vizzionnaire.server.dao.oauth2;

import com.vizzionnaire.server.common.data.oauth2.OAuth2Domain;
import com.vizzionnaire.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface OAuth2DomainDao extends Dao<OAuth2Domain> {

    List<OAuth2Domain> findByOAuth2ParamsId(UUID oauth2ParamsId);

}
