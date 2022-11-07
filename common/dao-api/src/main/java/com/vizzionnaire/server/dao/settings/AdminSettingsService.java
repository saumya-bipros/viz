package com.vizzionnaire.server.dao.settings;

import com.vizzionnaire.server.common.data.AdminSettings;
import com.vizzionnaire.server.common.data.id.AdminSettingsId;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface AdminSettingsService {

    AdminSettings findAdminSettingsById(TenantId tenantId, AdminSettingsId adminSettingsId);

    AdminSettings findAdminSettingsByKey(TenantId tenantId, String key);

    AdminSettings findAdminSettingsByTenantIdAndKey(TenantId tenantId, String key);

    AdminSettings saveAdminSettings(TenantId tenantId, AdminSettings adminSettings);

    boolean deleteAdminSettingsByTenantIdAndKey(TenantId tenantId, String key);

    void deleteAdminSettingsByTenantId(TenantId tenantId);

}
