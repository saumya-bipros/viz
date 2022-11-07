package com.vizzionnaire.server.dao.oauth2;

import com.vizzionnaire.server.common.data.oauth2.OAuth2Mobile;
import com.vizzionnaire.server.dao.Dao;

import java.util.List;
import java.util.UUID;

public interface OAuth2MobileDao extends Dao<OAuth2Mobile> {

    List<OAuth2Mobile> findByOAuth2ParamsId(UUID oauth2ParamsId);

}
