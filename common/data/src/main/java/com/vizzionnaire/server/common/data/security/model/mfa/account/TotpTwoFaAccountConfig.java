package com.vizzionnaire.server.common.data.security.model.mfa.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;

@Data
@EqualsAndHashCode(callSuper = true)
public class TotpTwoFaAccountConfig extends TwoFaAccountConfig {

    @NotBlank(message = "OTP auth URL cannot be blank")
    @Pattern(regexp = "otpauth://totp/(\\S+?):(\\S+?)\\?issuer=(\\S+?)&secret=(\\w+?)", message = "OTP auth url is invalid")
    private String authUrl;

    @Override
    public TwoFaProviderType getProviderType() {
        return TwoFaProviderType.TOTP;
    }

}

