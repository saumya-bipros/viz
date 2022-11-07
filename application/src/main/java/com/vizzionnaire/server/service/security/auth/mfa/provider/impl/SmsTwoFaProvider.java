package com.vizzionnaire.server.service.security.auth.mfa.provider.impl;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.vizzionnaire.rule.engine.api.SmsService;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.ThingsboardErrorCode;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.security.model.mfa.account.SmsTwoFaAccountConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.SmsTwoFaProviderConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.security.model.SecurityUser;

import java.util.Map;

@Service
@TbCoreComponent
public class SmsTwoFaProvider extends OtpBasedTwoFaProvider<SmsTwoFaProviderConfig, SmsTwoFaAccountConfig> {

    private final SmsService smsService;

    public SmsTwoFaProvider(CacheManager cacheManager, SmsService smsService) {
        super(cacheManager);
        this.smsService = smsService;
    }


    @Override
    public SmsTwoFaAccountConfig generateNewAccountConfig(User user, SmsTwoFaProviderConfig providerConfig) {
        return new SmsTwoFaAccountConfig();
    }

    @Override
    protected void sendVerificationCode(SecurityUser user, String verificationCode, SmsTwoFaProviderConfig providerConfig, SmsTwoFaAccountConfig accountConfig) throws ThingsboardException {
        Map<String, String> messageData = Map.of(
                "code", verificationCode,
                "userEmail", user.getEmail()
        );
        String message = TbNodeUtils.processTemplate(providerConfig.getSmsVerificationMessageTemplate(), messageData);
        String phoneNumber = accountConfig.getPhoneNumber();

        smsService.sendSms(user.getTenantId(), user.getCustomerId(), new String[]{phoneNumber}, message);
    }

    @Override
    public void check(TenantId tenantId) throws ThingsboardException {
        if (!smsService.isConfigured(tenantId)) {
            throw new ThingsboardException("SMS service is not configured", ThingsboardErrorCode.BAD_REQUEST_PARAMS);
        }
    }


    @Override
    public TwoFaProviderType getType() {
        return TwoFaProviderType.SMS;
    }

}
