package com.vizzionnaire.server.common.data.security;

import com.vizzionnaire.server.common.data.BaseData;
import com.vizzionnaire.server.common.data.id.UserAuthSettingsId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.model.mfa.account.AccountTwoFaSettings;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserAuthSettings extends BaseData<UserAuthSettingsId> {

    private static final long serialVersionUID = 2628320657987010348L;

    private UserId userId;
    private AccountTwoFaSettings twoFaSettings;

}
