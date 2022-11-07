package com.vizzionnaire.server.common.data.security.model.mfa.account;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class BackupCodeTwoFaAccountConfig extends TwoFaAccountConfig {

    @NotEmpty
    private Set<String> codes;

    @Override
    public TwoFaProviderType getProviderType() {
        return TwoFaProviderType.BACKUP_CODE;
    }


    @JsonGetter("codes")
    private Set<String> getCodesForJson() {
        if (serializeHiddenFields) {
            return codes;
        } else {
            return null;
        }
    }

    @JsonGetter
    private Integer getCodesLeft() {
        if (codes != null) {
            return codes.size();
        } else {
            return null;
        }
    }

}
