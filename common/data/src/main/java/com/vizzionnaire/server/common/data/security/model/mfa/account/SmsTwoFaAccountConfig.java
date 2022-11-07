package com.vizzionnaire.server.common.data.security.model.mfa.account;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;

@EqualsAndHashCode(callSuper = true)
@Data
public class SmsTwoFaAccountConfig extends OtpBasedTwoFaAccountConfig {

    @NotBlank(message = "phone number cannot be blank")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "phone number is not of E.164 format")
    private String phoneNumber;

    @Override
    public TwoFaProviderType getProviderType() {
        return TwoFaProviderType.SMS;
    }

}
