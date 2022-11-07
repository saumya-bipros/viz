package com.vizzionnaire.server.common.data.security.model.mfa.account;

import lombok.Data;

import java.util.LinkedHashMap;

import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;

@Data
public class AccountTwoFaSettings {
    private LinkedHashMap<TwoFaProviderType, TwoFaAccountConfig> configs;
}
