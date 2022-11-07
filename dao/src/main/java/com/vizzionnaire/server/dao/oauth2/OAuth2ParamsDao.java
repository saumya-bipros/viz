package com.vizzionnaire.server.dao.oauth2;

import com.vizzionnaire.server.common.data.oauth2.OAuth2Params;
import com.vizzionnaire.server.dao.Dao;

public interface OAuth2ParamsDao extends Dao<OAuth2Params> {
    void deleteAll();
}
