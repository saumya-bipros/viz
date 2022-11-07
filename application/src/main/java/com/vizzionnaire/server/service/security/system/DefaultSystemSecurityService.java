package com.vizzionnaire.server.service.security.system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.rule.engine.api.MailService;
import com.vizzionnaire.server.common.data.AdminSettings;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.security.UserCredentials;
import com.vizzionnaire.server.common.data.security.model.SecuritySettings;
import com.vizzionnaire.server.common.data.security.model.UserPasswordPolicy;
import com.vizzionnaire.server.common.data.security.model.mfa.PlatformTwoFaSettings;
import com.vizzionnaire.server.dao.audit.AuditLogService;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.settings.AdminSettingsService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.dao.user.UserServiceImpl;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.security.auth.rest.RestAuthenticationDetails;
import com.vizzionnaire.server.service.security.exception.UserPasswordExpiredException;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.utils.MiscUtils;

import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ua_parser.Client;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.vizzionnaire.server.common.data.CacheConstants.SECURITY_SETTINGS_CACHE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@TbCoreComponent
public class DefaultSystemSecurityService implements SystemSecurityService {

    @Autowired
    private AdminSettingsService adminSettingsService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AuditLogService auditLogService;

    @Resource
    private SystemSecurityService self;

    @Cacheable(cacheNames = SECURITY_SETTINGS_CACHE, key = "'securitySettings'")
    @Override
    public SecuritySettings getSecuritySettings(TenantId tenantId) {
        SecuritySettings securitySettings = null;
        AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(tenantId, "securitySettings");
        if (adminSettings != null) {
            try {
                securitySettings = JacksonUtil.convertValue(adminSettings.getJsonValue(), SecuritySettings.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load security settings!", e);
            }
        } else {
            securitySettings = new SecuritySettings();
            securitySettings.setPasswordPolicy(new UserPasswordPolicy());
            securitySettings.getPasswordPolicy().setMinimumLength(6);
        }
        return securitySettings;
    }

    @CacheEvict(cacheNames = SECURITY_SETTINGS_CACHE, key = "'securitySettings'")
    @Override
    public SecuritySettings saveSecuritySettings(TenantId tenantId, SecuritySettings securitySettings) {
        AdminSettings adminSettings = adminSettingsService.findAdminSettingsByKey(tenantId, "securitySettings");
        if (adminSettings == null) {
            adminSettings = new AdminSettings();
            adminSettings.setTenantId(tenantId);
            adminSettings.setKey("securitySettings");
        }
        adminSettings.setJsonValue(JacksonUtil.valueToTree(securitySettings));
        AdminSettings savedAdminSettings = adminSettingsService.saveAdminSettings(tenantId, adminSettings);
        try {
            return JacksonUtil.convertValue(savedAdminSettings.getJsonValue(), SecuritySettings.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load security settings!", e);
        }
    }

    @Override
    public void validateUserCredentials(TenantId tenantId, UserCredentials userCredentials, String username, String password) throws AuthenticationException {
        if (!encoder.matches(password, userCredentials.getPassword())) {
            int failedLoginAttempts = userService.increaseFailedLoginAttempts(tenantId, userCredentials.getUserId());
            SecuritySettings securitySettings = self.getSecuritySettings(tenantId);
            if (securitySettings.getMaxFailedLoginAttempts() != null && securitySettings.getMaxFailedLoginAttempts() > 0) {
                if (failedLoginAttempts > securitySettings.getMaxFailedLoginAttempts() && userCredentials.isEnabled()) {
                    lockAccount(userCredentials.getUserId(), username, securitySettings.getUserLockoutNotificationEmail(), securitySettings.getMaxFailedLoginAttempts());
                    throw new LockedException("Authentication Failed. Username was locked due to security policy.");
                }
            }
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        if (!userCredentials.isEnabled()) {
            throw new DisabledException("User is not active");
        }

        userService.resetFailedLoginAttempts(tenantId, userCredentials.getUserId());

        SecuritySettings securitySettings = self.getSecuritySettings(tenantId);
        if (isPositiveInteger(securitySettings.getPasswordPolicy().getPasswordExpirationPeriodDays())) {
            if ((userCredentials.getCreatedTime()
                    + TimeUnit.DAYS.toMillis(securitySettings.getPasswordPolicy().getPasswordExpirationPeriodDays()))
                    < System.currentTimeMillis()) {
                userCredentials = userService.requestExpiredPasswordReset(tenantId, userCredentials.getId());
                throw new UserPasswordExpiredException("User password expired!", userCredentials.getResetToken());
            }
        }
    }

    @Override
    public void validateTwoFaVerification(SecurityUser securityUser, boolean verificationSuccess, PlatformTwoFaSettings twoFaSettings) {
        TenantId tenantId = securityUser.getTenantId();
        UserId userId = securityUser.getId();

        int failedVerificationAttempts;
        if (!verificationSuccess) {
            failedVerificationAttempts = userService.increaseFailedLoginAttempts(tenantId, userId);
        } else {
            userService.resetFailedLoginAttempts(tenantId, userId);
            return;
        }

        Integer maxVerificationFailures = twoFaSettings.getMaxVerificationFailuresBeforeUserLockout();
        if (maxVerificationFailures != null && maxVerificationFailures > 0
                && failedVerificationAttempts >= maxVerificationFailures) {
            userService.setUserCredentialsEnabled(TenantId.SYS_TENANT_ID, userId, false);
            SecuritySettings securitySettings = self.getSecuritySettings(tenantId);
            lockAccount(userId, securityUser.getEmail(), securitySettings.getUserLockoutNotificationEmail(), maxVerificationFailures);
            throw new LockedException("User account was locked due to exceeded 2FA verification attempts");
        }
    }

    private void lockAccount(UserId userId, String username, String userLockoutNotificationEmail, Integer maxFailedLoginAttempts) {
        userService.setUserCredentialsEnabled(TenantId.SYS_TENANT_ID, userId, false);
        if (StringUtils.isNotBlank(userLockoutNotificationEmail)) {
            try {
                mailService.sendAccountLockoutEmail(username, userLockoutNotificationEmail, maxFailedLoginAttempts);
            } catch (VizzionnaireException e) {
                log.warn("Can't send email regarding user account [{}] lockout to provided email [{}]", username, userLockoutNotificationEmail, e);
            }
        }
    }

    @Override
    public void validatePassword(TenantId tenantId, String password, UserCredentials userCredentials) throws DataValidationException {
        SecuritySettings securitySettings = self.getSecuritySettings(tenantId);
        UserPasswordPolicy passwordPolicy = securitySettings.getPasswordPolicy();

        List<Rule> passwordRules = new ArrayList<>();
        passwordRules.add(new LengthRule(passwordPolicy.getMinimumLength(), Integer.MAX_VALUE));
        if (isPositiveInteger(passwordPolicy.getMinimumUppercaseLetters())) {
            passwordRules.add(new CharacterRule(EnglishCharacterData.UpperCase, passwordPolicy.getMinimumUppercaseLetters()));
        }
        if (isPositiveInteger(passwordPolicy.getMinimumLowercaseLetters())) {
            passwordRules.add(new CharacterRule(EnglishCharacterData.LowerCase, passwordPolicy.getMinimumLowercaseLetters()));
        }
        if (isPositiveInteger(passwordPolicy.getMinimumDigits())) {
            passwordRules.add(new CharacterRule(EnglishCharacterData.Digit, passwordPolicy.getMinimumDigits()));
        }
        if (isPositiveInteger(passwordPolicy.getMinimumSpecialCharacters())) {
            passwordRules.add(new CharacterRule(EnglishCharacterData.Special, passwordPolicy.getMinimumSpecialCharacters()));
        }
        if (passwordPolicy.getAllowWhitespaces() != null && !passwordPolicy.getAllowWhitespaces()) {
            passwordRules.add(new WhitespaceRule());
        }
        PasswordValidator validator = new PasswordValidator(passwordRules);
        PasswordData passwordData = new PasswordData(password);
        RuleResult result = validator.validate(passwordData);
        if (!result.isValid()) {
            String message = String.join("\n", validator.getMessages(result));
            throw new DataValidationException(message);
        }

        if (userCredentials != null && isPositiveInteger(passwordPolicy.getPasswordReuseFrequencyDays())) {
            long passwordReuseFrequencyTs = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(passwordPolicy.getPasswordReuseFrequencyDays());
            User user = userService.findUserById(tenantId, userCredentials.getUserId());
            JsonNode additionalInfo = user.getAdditionalInfo();
            if (additionalInfo instanceof ObjectNode && additionalInfo.has(UserServiceImpl.USER_PASSWORD_HISTORY)) {
                JsonNode userPasswordHistoryJson = additionalInfo.get(UserServiceImpl.USER_PASSWORD_HISTORY);
                Map<String, String> userPasswordHistoryMap = JacksonUtil.convertValue(userPasswordHistoryJson, new TypeReference<>() {});
                for (Map.Entry<String, String> entry : userPasswordHistoryMap.entrySet()) {
                    if (encoder.matches(password, entry.getValue()) && Long.parseLong(entry.getKey()) > passwordReuseFrequencyTs) {
                        throw new DataValidationException("Password was already used for the last " + passwordPolicy.getPasswordReuseFrequencyDays() + " days");
                    }
                }

            }
        }
    }

    @Override
    public String getBaseUrl(TenantId tenantId, CustomerId customerId, HttpServletRequest httpServletRequest) {
        String baseUrl = null;
        AdminSettings generalSettings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, "general");

        JsonNode prohibitDifferentUrl = generalSettings.getJsonValue().get("prohibitDifferentUrl");

        if (prohibitDifferentUrl != null && prohibitDifferentUrl.asBoolean()) {
            baseUrl = generalSettings.getJsonValue().get("baseUrl").asText();
        }

        if (StringUtils.isEmpty(baseUrl)) {
            baseUrl = MiscUtils.constructBaseUrl(httpServletRequest);
        }

        return baseUrl;
    }

    @Override
    public void logLoginAction(User user, Object authenticationDetails, ActionType actionType, Exception e) {
        String clientAddress = "Unknown";
        String browser = "Unknown";
        String os = "Unknown";
        String device = "Unknown";
        if (authenticationDetails instanceof RestAuthenticationDetails) {
            RestAuthenticationDetails details = (RestAuthenticationDetails) authenticationDetails;
            clientAddress = details.getClientAddress();
            if (details.getUserAgent() != null) {
                Client userAgent = details.getUserAgent();
                if (userAgent.userAgent != null) {
                    browser = userAgent.userAgent.family;
                    if (userAgent.userAgent.major != null) {
                        browser += " " + userAgent.userAgent.major;
                        if (userAgent.userAgent.minor != null) {
                            browser += "." + userAgent.userAgent.minor;
                                if (userAgent.userAgent.patch != null) {
                                    browser += "." + userAgent.userAgent.patch;
                                }
                            }
                        }
                    }
                    if (userAgent.os != null) {
                        os = userAgent.os.family;
                        if (userAgent.os.major != null) {
                            os += " " + userAgent.os.major;
                            if (userAgent.os.minor != null) {
                                os += "." + userAgent.os.minor;
                                if (userAgent.os.patch != null) {
                                    os += "." + userAgent.os.patch;
                                    if (userAgent.os.patchMinor != null) {
                                        os += "." + userAgent.os.patchMinor;
                                    }
                                }
                            }
                        }
                    }
                    if (userAgent.device != null) {
                        device = userAgent.device.family;
                    }
                }
            }
        if (actionType == ActionType.LOGIN && e == null) {
            userService.setLastLoginTs(user.getTenantId(), user.getId());
        }
        auditLogService.logEntityAction(
                user.getTenantId(), user.getCustomerId(), user.getId(),
                user.getName(), user.getId(), null, actionType, e, clientAddress, browser, os, device);
    }

    private static boolean isPositiveInteger(Integer val) {
        return val != null && val.intValue() > 0;
    }
}
