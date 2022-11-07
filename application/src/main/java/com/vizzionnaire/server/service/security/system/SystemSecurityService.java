package com.vizzionnaire.server.service.security.system;

import org.springframework.security.core.AuthenticationException;

import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.security.UserCredentials;
import com.vizzionnaire.server.common.data.security.model.SecuritySettings;
import com.vizzionnaire.server.common.data.security.model.mfa.PlatformTwoFaSettings;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.service.security.model.SecurityUser;

import javax.servlet.http.HttpServletRequest;

public interface SystemSecurityService {

    SecuritySettings getSecuritySettings(TenantId tenantId);

    SecuritySettings saveSecuritySettings(TenantId tenantId, SecuritySettings securitySettings);

    void validateUserCredentials(TenantId tenantId, UserCredentials userCredentials, String username, String password) throws AuthenticationException;

    void validateTwoFaVerification(SecurityUser securityUser, boolean verificationSuccess, PlatformTwoFaSettings twoFaSettings);

    void validatePassword(TenantId tenantId, String password, UserCredentials userCredentials) throws DataValidationException;

    String getBaseUrl(TenantId tenantId, CustomerId customerId, HttpServletRequest httpServletRequest);

    void logLoginAction(User user, Object authenticationDetails, ActionType actionType, Exception e);

}
