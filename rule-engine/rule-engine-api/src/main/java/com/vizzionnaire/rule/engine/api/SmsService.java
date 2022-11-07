package com.vizzionnaire.rule.engine.api;

import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sms.config.TestSmsRequest;

public interface SmsService {

    void updateSmsConfiguration();

    void sendSms(TenantId tenantId, CustomerId customerId, String[] numbersTo, String message) throws ThingsboardException;;

    void sendTestSms(TestSmsRequest testSmsRequest) throws ThingsboardException;

    boolean isConfigured(TenantId tenantId);

}
