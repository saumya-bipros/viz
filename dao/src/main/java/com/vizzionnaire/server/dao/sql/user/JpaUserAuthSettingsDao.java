package com.vizzionnaire.server.dao.sql.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.UserAuthSettings;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.UserAuthSettingsEntity;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;
import com.vizzionnaire.server.dao.user.UserAuthSettingsDao;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaUserAuthSettingsDao extends JpaAbstractDao<UserAuthSettingsEntity, UserAuthSettings> implements UserAuthSettingsDao {

    private final UserAuthSettingsRepository repository;

    @Override
    public UserAuthSettings findByUserId(UserId userId) {
        return DaoUtil.getData(repository.findByUserId(userId.getId()));
    }

    @Override
    public void removeByUserId(UserId userId) {
        repository.deleteByUserId(userId.getId());
    }

    @Override
    protected Class<UserAuthSettingsEntity> getEntityClass() {
        return UserAuthSettingsEntity.class;
    }

    @Override
    protected JpaRepository<UserAuthSettingsEntity, UUID> getRepository() {
        return repository;
    }

}
