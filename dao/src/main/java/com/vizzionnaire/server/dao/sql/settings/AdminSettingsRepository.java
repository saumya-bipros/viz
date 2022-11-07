package com.vizzionnaire.server.dao.sql.settings;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vizzionnaire.server.dao.model.sql.AdminSettingsEntity;

import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public interface AdminSettingsRepository extends JpaRepository<AdminSettingsEntity, UUID> {

    AdminSettingsEntity findByTenantIdAndKey(UUID tenantId, String key);

    void deleteByTenantIdAndKey(UUID tenantId, String key);

    void deleteByTenantId(UUID tenantId);

    boolean existsByTenantIdAndKey(UUID tenantId, String key);

}
