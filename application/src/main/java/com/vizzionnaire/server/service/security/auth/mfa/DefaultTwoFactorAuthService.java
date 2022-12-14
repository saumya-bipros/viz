package com.vizzionnaire.server.service.security.auth.mfa;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.model.mfa.PlatformTwoFaSettings;
import com.vizzionnaire.server.common.data.security.model.mfa.account.TwoFaAccountConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderConfig;
import com.vizzionnaire.server.common.data.security.model.mfa.provider.TwoFaProviderType;
import com.vizzionnaire.server.common.msg.tools.TbRateLimits;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.security.auth.mfa.config.TwoFaConfigManager;
import com.vizzionnaire.server.service.security.auth.mfa.provider.TwoFaProvider;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.service.security.system.SystemSecurityService;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@TbCoreComponent
public class DefaultTwoFactorAuthService implements TwoFactorAuthService {

    private final TwoFaConfigManager configManager;
    private final SystemSecurityService systemSecurityService;
    private final UserService userService;
    private final Map<TwoFaProviderType, TwoFaProvider<TwoFaProviderConfig, TwoFaAccountConfig>> providers = new EnumMap<>(TwoFaProviderType.class);

    private static final VizzionnaireException ACCOUNT_NOT_CONFIGURED_ERROR = new VizzionnaireException("2FA is not configured for account", VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
    private static final VizzionnaireException PROVIDER_NOT_CONFIGURED_ERROR = new VizzionnaireException("2FA provider is not configured", VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
    private static final VizzionnaireException PROVIDER_NOT_AVAILABLE_ERROR = new VizzionnaireException("2FA provider is not available", VizzionnaireErrorCode.GENERAL);

    private final ConcurrentMap<UserId, ConcurrentMap<TwoFaProviderType, TbRateLimits>> verificationCodeSendingRateLimits = new ConcurrentHashMap<>();
    private final ConcurrentMap<UserId, ConcurrentMap<TwoFaProviderType, TbRateLimits>> verificationCodeCheckingRateLimits = new ConcurrentHashMap<>();

    @Override
    public boolean isTwoFaEnabled(TenantId tenantId, UserId userId) {
        return configManager.getAccountTwoFaSettings(tenantId, userId)
                .map(settings -> !settings.getConfigs().isEmpty())
                .orElse(false);
    }

    @Override
    public void checkProvider(TenantId tenantId, TwoFaProviderType providerType) throws VizzionnaireException {
        getTwoFaProvider(providerType).check(tenantId);
    }


    @Override
    public void prepareVerificationCode(SecurityUser user, TwoFaProviderType providerType, boolean checkLimits) throws Exception {
        TwoFaAccountConfig accountConfig = configManager.getTwoFaAccountConfig(user.getTenantId(), user.getId(), providerType)
                .orElseThrow(() -> ACCOUNT_NOT_CONFIGURED_ERROR);
        prepareVerificationCode(user, accountConfig, checkLimits);
    }

    @Override
    public void prepareVerificationCode(SecurityUser user, TwoFaAccountConfig accountConfig, boolean checkLimits) throws VizzionnaireException {
        PlatformTwoFaSettings twoFaSettings = configManager.getPlatformTwoFaSettings(user.getTenantId(), true)
                .orElseThrow(() -> PROVIDER_NOT_CONFIGURED_ERROR);
        if (checkLimits) {
            Integer minVerificationCodeSendPeriod = twoFaSettings.getMinVerificationCodeSendPeriod();
            String rateLimit = null;
            if (minVerificationCodeSendPeriod != null && minVerificationCodeSendPeriod > 4) {
                rateLimit = "1:" + minVerificationCodeSendPeriod;
            }
            checkRateLimits(user.getId(), accountConfig.getProviderType(), rateLimit, verificationCodeSendingRateLimits);
        }

        TwoFaProviderConfig providerConfig = twoFaSettings.getProviderConfig(accountConfig.getProviderType())
                .orElseThrow(() -> PROVIDER_NOT_CONFIGURED_ERROR);
        getTwoFaProvider(accountConfig.getProviderType()).prepareVerificationCode(user, providerConfig, accountConfig);
    }


    @Override
    public boolean checkVerificationCode(SecurityUser user, TwoFaProviderType providerType, String verificationCode, boolean checkLimits) throws VizzionnaireException {
        TwoFaAccountConfig accountConfig = configManager.getTwoFaAccountConfig(user.getTenantId(), user.getId(), providerType)
                .orElseThrow(() -> ACCOUNT_NOT_CONFIGURED_ERROR);
        return checkVerificationCode(user, verificationCode, accountConfig, checkLimits);
    }

    @Override
    public boolean checkVerificationCode(SecurityUser user, String verificationCode, TwoFaAccountConfig accountConfig, boolean checkLimits) throws VizzionnaireException {
        if (!userService.findUserCredentialsByUserId(user.getTenantId(), user.getId()).isEnabled()) {
            throw new VizzionnaireException("User is disabled", VizzionnaireErrorCode.AUTHENTICATION);
        }

        PlatformTwoFaSettings twoFaSettings = configManager.getPlatformTwoFaSettings(user.getTenantId(), true)
                .orElseThrow(() -> PROVIDER_NOT_CONFIGURED_ERROR);
        if (checkLimits) {
            checkRateLimits(user.getId(), accountConfig.getProviderType(), twoFaSettings.getVerificationCodeCheckRateLimit(), verificationCodeCheckingRateLimits);
        }
        TwoFaProviderConfig providerConfig = twoFaSettings.getProviderConfig(accountConfig.getProviderType())
                .orElseThrow(() -> PROVIDER_NOT_CONFIGURED_ERROR);

        boolean verificationSuccess = false;
        if (StringUtils.isNotBlank(verificationCode)) {
            if (StringUtils.isNumeric(verificationCode) || accountConfig.getProviderType() == TwoFaProviderType.BACKUP_CODE) {
                verificationSuccess = getTwoFaProvider(accountConfig.getProviderType()).checkVerificationCode(user, verificationCode, providerConfig, accountConfig);
            }
        }
        if (checkLimits) {
            try {
                systemSecurityService.validateTwoFaVerification(user, verificationSuccess, twoFaSettings);
            } catch (LockedException e) {
                verificationCodeCheckingRateLimits.remove(user.getId());
                verificationCodeSendingRateLimits.remove(user.getId());
                throw new VizzionnaireException(e.getMessage(), VizzionnaireErrorCode.AUTHENTICATION);
            }
            if (verificationSuccess) {
                verificationCodeCheckingRateLimits.remove(user.getId());
                verificationCodeSendingRateLimits.remove(user.getId());
            }
        }
        return verificationSuccess;
    }

    private void checkRateLimits(UserId userId, TwoFaProviderType providerType, String rateLimitConfig,
                                 ConcurrentMap<UserId, ConcurrentMap<TwoFaProviderType, TbRateLimits>> rateLimits) throws VizzionnaireException {
        if (StringUtils.isNotEmpty(rateLimitConfig)) {
            ConcurrentMap<TwoFaProviderType, TbRateLimits> providersRateLimits = rateLimits.computeIfAbsent(userId, i -> new ConcurrentHashMap<>());

            TbRateLimits rateLimit = providersRateLimits.get(providerType);
            if (rateLimit == null || !rateLimit.getConfiguration().equals(rateLimitConfig)) {
                rateLimit = new TbRateLimits(rateLimitConfig, true);
                providersRateLimits.put(providerType, rateLimit);
            }
            if (!rateLimit.tryConsume()) {
                throw new VizzionnaireException("Too many requests", VizzionnaireErrorCode.TOO_MANY_REQUESTS);
            }
        } else {
            rateLimits.remove(userId);
        }
    }


    @Override
    public TwoFaAccountConfig generateNewAccountConfig(User user, TwoFaProviderType providerType) throws VizzionnaireException {
        TwoFaProviderConfig providerConfig = getTwoFaProviderConfig(user.getTenantId(), providerType);
        return getTwoFaProvider(providerType).generateNewAccountConfig(user, providerConfig);
    }


    private TwoFaProviderConfig getTwoFaProviderConfig(TenantId tenantId, TwoFaProviderType providerType) throws VizzionnaireException {
        return configManager.getPlatformTwoFaSettings(tenantId, true)
                .flatMap(twoFaSettings -> twoFaSettings.getProviderConfig(providerType))
                .orElseThrow(() -> PROVIDER_NOT_CONFIGURED_ERROR);
    }

    private TwoFaProvider<TwoFaProviderConfig, TwoFaAccountConfig> getTwoFaProvider(TwoFaProviderType providerType) throws VizzionnaireException {
        return Optional.ofNullable(providers.get(providerType))
                .orElseThrow(() -> PROVIDER_NOT_AVAILABLE_ERROR);
    }

    @Autowired
    private void setProviders(Collection<TwoFaProvider> providers) {
        providers.forEach(provider -> {
            this.providers.put(provider.getType(), provider);
        });
    }

}
