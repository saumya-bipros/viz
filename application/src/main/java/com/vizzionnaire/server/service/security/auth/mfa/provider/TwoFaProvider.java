package com.vizzionnaire.server.service.security.auth.mfa.provider;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.security.model.mfa.account.TwoFaAccountConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;
import com.vizzionnaire.server.service.security.model.SecurityUser;

public interface TwoFaProvider<C extends TwoFaProviderConfig, A extends TwoFaAccountConfig> {

    A generateNewAccountConfig(User user, C providerConfig);

    default void prepareVerificationCode(SecurityUser user, C providerConfig, A accountConfig) throws VizzionnaireException {}

    boolean checkVerificationCode(SecurityUser user, String code, C providerConfig, A accountConfig);

    default void check(TenantId tenantId) throws VizzionnaireException {};


    TwoFaProviderType getType();

}
