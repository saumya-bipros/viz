package com.vizzionnaire.server.service.security.auth.mfa.config;

import java.util.Optional;

import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.model.mfa.PlatformTwoFaSettings;
import com.vizzionnaire.server.common.data.security.model.mfa.account.AccountTwoFaSettings;
import com.vizzionnaire.server.common.data.security.model.mfa.account.TwoFaAccountConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;

public interface TwoFaConfigManager {

    Optional<AccountTwoFaSettings> getAccountTwoFaSettings(TenantId tenantId, UserId userId);


    Optional<TwoFaAccountConfig> getTwoFaAccountConfig(TenantId tenantId, UserId userId, TwoFaProviderType providerType);

    AccountTwoFaSettings saveTwoFaAccountConfig(TenantId tenantId, UserId userId, TwoFaAccountConfig accountConfig);

    AccountTwoFaSettings deleteTwoFaAccountConfig(TenantId tenantId, UserId userId, TwoFaProviderType providerType);


    Optional<PlatformTwoFaSettings> getPlatformTwoFaSettings(TenantId tenantId, boolean sysadminSettingsAsDefault);

    PlatformTwoFaSettings savePlatformTwoFaSettings(TenantId tenantId, PlatformTwoFaSettings twoFactorAuthSettings) throws ThingsboardException;

    void deletePlatformTwoFaSettings(TenantId tenantId);

}
