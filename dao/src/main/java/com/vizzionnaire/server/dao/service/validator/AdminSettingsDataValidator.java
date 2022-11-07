package com.vizzionnaire.server.dao.service.validator;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.AdminSettings;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.settings.AdminSettingsService;

@Component
@AllArgsConstructor
public class AdminSettingsDataValidator extends DataValidator<AdminSettings> {

    private final AdminSettingsService adminSettingsService;

    @Override
    protected void validateCreate(TenantId tenantId, AdminSettings adminSettings) {
        AdminSettings existentAdminSettingsWithKey = adminSettingsService.findAdminSettingsByKey(tenantId, adminSettings.getKey());
        if (existentAdminSettingsWithKey != null) {
            throw new DataValidationException("Admin settings with such name already exists!");
        }
    }

    @Override
    protected AdminSettings validateUpdate(TenantId tenantId, AdminSettings adminSettings) {
        AdminSettings existentAdminSettings = adminSettingsService.findAdminSettingsById(tenantId, adminSettings.getId());
        if (existentAdminSettings != null) {
            if (!existentAdminSettings.getKey().equals(adminSettings.getKey())) {
                throw new DataValidationException("Changing key of admin settings entry is prohibited!");
            }
        }
        return existentAdminSettings;
    }


    @Override
    protected void validateDataImpl(TenantId tenantId, AdminSettings adminSettings) {
        if (StringUtils.isEmpty(adminSettings.getKey())) {
            throw new DataValidationException("Key should be specified!");
        }
        if (adminSettings.getJsonValue() == null) {
            throw new DataValidationException("Json value should be specified!");
        }
    }
}
