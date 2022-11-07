package com.vizzionnaire.server.service.sms;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.rule.engine.api.SmsService;
import com.vizzionnaire.rule.engine.api.sms.SmsSender;
import com.vizzionnaire.rule.engine.api.sms.SmsSenderFactory;
import com.vizzionnaire.server.common.data.AdminSettings;
import com.vizzionnaire.server.common.data.ApiUsageRecordKey;
import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sms.config.SmsProviderConfiguration;
import com.vizzionnaire.server.common.data.sms.config.TestSmsRequest;
import com.vizzionnaire.server.dao.settings.AdminSettingsService;
import com.vizzionnaire.server.queue.usagestats.TbApiUsageClient;
import com.vizzionnaire.server.service.apiusage.TbApiUsageStateService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedRuntimeException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
@Slf4j
public class DefaultSmsService implements SmsService {

    private final SmsSenderFactory smsSenderFactory;
    private final AdminSettingsService adminSettingsService;
    private final TbApiUsageStateService apiUsageStateService;
    private final TbApiUsageClient apiUsageClient;

    private SmsSender smsSender;

    public DefaultSmsService(SmsSenderFactory smsSenderFactory, AdminSettingsService adminSettingsService, TbApiUsageStateService apiUsageStateService, TbApiUsageClient apiUsageClient) {
        this.smsSenderFactory = smsSenderFactory;
        this.adminSettingsService = adminSettingsService;
        this.apiUsageStateService = apiUsageStateService;
        this.apiUsageClient = apiUsageClient;
    }

    @PostConstruct
    private void init() {
        updateSmsConfiguration();
    }

    @PreDestroy
    private void destroy() {
        if (this.smsSender != null) {
            this.smsSender.destroy();
        }
    }

    @Override
    public void updateSmsConfiguration() {
        AdminSettings settings = adminSettingsService.findAdminSettingsByKey(TenantId.SYS_TENANT_ID, "sms");
        if (settings != null) {
            try {
                JsonNode jsonConfig = settings.getJsonValue();
                SmsProviderConfiguration configuration = JacksonUtil.convertValue(jsonConfig, SmsProviderConfiguration.class);
                SmsSender newSmsSender = this.smsSenderFactory.createSmsSender(configuration);
                if (this.smsSender != null) {
                    this.smsSender.destroy();
                }
                this.smsSender = newSmsSender;
            } catch (Exception e) {
                log.error("Failed to create SMS sender", e);
            }
        }
    }

    private int sendSms(String numberTo, String message) throws VizzionnaireException {
        if (this.smsSender == null) {
            throw new VizzionnaireException("Unable to send SMS: no SMS provider configured!", VizzionnaireErrorCode.GENERAL);
        }
        return this.sendSms(this.smsSender, numberTo, message);
    }

    @Override
    public void sendSms(TenantId tenantId, CustomerId customerId, String[] numbersTo, String message) throws VizzionnaireException {
        if (apiUsageStateService.getApiUsageState(tenantId).isSmsSendEnabled()) {
            int smsCount = 0;
            try {
                for (String numberTo : numbersTo) {
                    smsCount += this.sendSms(numberTo, message);
                }
            } finally {
                if (smsCount > 0) {
                    apiUsageClient.report(tenantId, customerId, ApiUsageRecordKey.SMS_EXEC_COUNT, smsCount);
                }
            }
        } else {
            throw new RuntimeException("SMS sending is disabled due to API limits!");
        }
    }

    @Override
    public void sendTestSms(TestSmsRequest testSmsRequest) throws VizzionnaireException {
        SmsSender testSmsSender;
        try {
            testSmsSender = this.smsSenderFactory.createSmsSender(testSmsRequest.getProviderConfiguration());
        } catch (Exception e) {
            throw handleException(e);
        }
        this.sendSms(testSmsSender, testSmsRequest.getNumberTo(), testSmsRequest.getMessage());
        testSmsSender.destroy();
    }

    @Override
    public boolean isConfigured(TenantId tenantId) {
        return smsSender != null;
    }

    private int sendSms(SmsSender smsSender, String numberTo, String message) throws VizzionnaireException {
        try {
            return smsSender.sendSms(numberTo, message);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    private VizzionnaireException handleException(Exception exception) {
        String message;
        if (exception instanceof NestedRuntimeException) {
            message = ((NestedRuntimeException) exception).getMostSpecificCause().getMessage();
        } else {
            message = exception.getMessage();
        }
        log.warn("Unable to send SMS: {}", message);
        return new VizzionnaireException(String.format("Unable to send SMS: %s", message),
                VizzionnaireErrorCode.GENERAL);
    }
}
