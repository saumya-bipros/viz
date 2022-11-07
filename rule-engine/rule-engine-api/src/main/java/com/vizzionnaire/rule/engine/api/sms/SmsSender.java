package com.vizzionnaire.rule.engine.api.sms;

import com.vizzionnaire.rule.engine.api.sms.exception.SmsException;

public interface SmsSender {

    int sendSms(String numberTo, String message) throws SmsException;

    void destroy();

}
