package com.vizzionnaire.server.service.security.auth.mfa;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.model.mfa.account.TwoFaAccountConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;
import com.vizzionnaire.server.service.security.model.SecurityUser;

public interface TwoFactorAuthService {

    boolean isTwoFaEnabled(TenantId tenantId, UserId userId);

    void checkProvider(TenantId tenantId, TwoFaProviderType providerType) throws ThingsboardException;


    void prepareVerificationCode(SecurityUser user, TwoFaProviderType providerType, boolean checkLimits) throws Exception;

    void prepareVerificationCode(SecurityUser user, TwoFaAccountConfig accountConfig, boolean checkLimits) throws ThingsboardException;


    boolean checkVerificationCode(SecurityUser user, TwoFaProviderType providerType, String verificationCode, boolean checkLimits) throws ThingsboardException;

    boolean checkVerificationCode(SecurityUser user, String verificationCode, TwoFaAccountConfig accountConfig, boolean checkLimits) throws ThingsboardException;


    TwoFaAccountConfig generateNewAccountConfig(User user, TwoFaProviderType providerType) throws ThingsboardException;

}
