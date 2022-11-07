package com.vizzionnaire.server.dao.user;

import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.UserAuthSettings;
import com.vizzionnaire.server.dao.Dao;

public interface UserAuthSettingsDao extends Dao<UserAuthSettings> {

    UserAuthSettings findByUserId(UserId userId);

    void removeByUserId(UserId userId);

}
