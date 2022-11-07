package com.vizzionnaire.rule.engine.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.server.common.data.ApiFeature;
import com.vizzionnaire.server.common.data.ApiUsageStateMailMessage;
import com.vizzionnaire.server.common.data.ApiUsageStateValue;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;

import org.springframework.mail.javamail.JavaMailSender;

public interface MailService {

    void updateMailConfiguration();

    void sendEmail(TenantId tenantId, String email, String subject, String message) throws ThingsboardException;

    void sendTestMail(JsonNode config, String email) throws ThingsboardException;

    void sendActivationEmail(String activationLink, String email) throws ThingsboardException;

    void sendAccountActivatedEmail(String loginLink, String email) throws ThingsboardException;

    void sendResetPasswordEmail(String passwordResetLink, String email) throws ThingsboardException;

    void sendResetPasswordEmailAsync(String passwordResetLink, String email);

    void sendPasswordWasResetEmail(String loginLink, String email) throws ThingsboardException;

    void sendAccountLockoutEmail(String lockoutEmail, String email, Integer maxFailedLoginAttempts) throws ThingsboardException;

    void sendTwoFaVerificationEmail(String email, String verificationCode, int expirationTimeSeconds) throws ThingsboardException;

    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail) throws ThingsboardException;
    void send(TenantId tenantId, CustomerId customerId, TbEmail tbEmail, JavaMailSender javaMailSender, long timeout) throws ThingsboardException;

    void sendApiFeatureStateEmail(ApiFeature apiFeature, ApiUsageStateValue stateValue, String email, ApiUsageStateMailMessage msg) throws ThingsboardException;

    void testConnection(TenantId tenantId) throws Exception;

}
