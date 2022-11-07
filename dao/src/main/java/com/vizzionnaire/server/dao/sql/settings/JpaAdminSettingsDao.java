package com.vizzionnaire.server.dao.sql.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vizzionnaire.server.common.data.AdminSettings;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.AdminSettingsEntity;
import com.vizzionnaire.server.dao.settings.AdminSettingsDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import java.util.UUID;

@Component
@Slf4j
public class JpaAdminSettingsDao extends JpaAbstractDao<AdminSettingsEntity, AdminSettings> implements AdminSettingsDao {

    @Autowired
    private AdminSettingsRepository adminSettingsRepository;

    @Override
    protected Class<AdminSettingsEntity> getEntityClass() {
        return AdminSettingsEntity.class;
    }

    @Override
    protected JpaRepository<AdminSettingsEntity, UUID> getRepository() {
        return adminSettingsRepository;
    }

    @Override
    public AdminSettings findByTenantIdAndKey(UUID tenantId, String key) {
        return DaoUtil.getData(adminSettingsRepository.findByTenantIdAndKey(tenantId, key));
    }

    @Override
    @Transactional
    public boolean removeByTenantIdAndKey(UUID tenantId, String key) {
        if (adminSettingsRepository.existsByTenantIdAndKey(tenantId, key)) {
            adminSettingsRepository.deleteByTenantIdAndKey(tenantId, key);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void removeByTenantId(UUID tenantId) {
        adminSettingsRepository.deleteByTenantId(tenantId);
    }

}
