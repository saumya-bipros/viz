package com.vizzionnaire.rule.engine.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.server.common.data.ApiFeature;
import com.vizzionnaire.server.common.data.ApiUsageStateMailMessage;
import com.vizzionnaire.server.common.data.ApiUsageStateValue;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;

import org.springframework.mail.javamail.JavaMailSender;

public interface MailService {

    void updateMailConfiguration();

    void sendEmail(TenantId tenantId, String email, String subject, String message) throws VizzionnaireException;

    void sendTestMail(JsonNode config, String email) throws VizzionnaireException;

    void sendActivationEmail(String activationLink, String email) throws VizzionnaireException;

    void sendAccountActivatedEmail(String loginLink, String email) throws VizzionnaireException;

    void sendResetPasswordEmail(String passwordResetLink, String email) throws VizzionnaireException;

    void sendResetPasswordEmailAsync(String passwordResetLink, String email);

    void sendPasswordWasResetEmail(String loginLink, String email) throws VizzionnaireException;

    void sendAccountLockoutEmail(String lockoutEmail, String email, Integer maxFailedLoginAttempts) throws VizzionnaireException;

    void sendTwoFaVerificationEmail(String email, String verificationCode, int expirationTimeSeconds) throws VizzionnaireException;

    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail) throws VizzionnaireException;
    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail, JavaMailSender javaMailSender, long timeout) throws VizzionnaireException;

    void sendApiFeatureStateEmail(ApiFeature apiFeature, ApiUsageStateValue stateValue, String email, ApiUsageStateMailMessage msg) throws VizzionnaireException;

    void testConnection(TenantId tenantId) throws Exception;

}
