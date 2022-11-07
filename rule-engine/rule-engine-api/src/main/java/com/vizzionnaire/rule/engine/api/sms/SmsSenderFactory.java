package com.vizzionnaire.rule.engine.api.sms;

import com.vizzionnaire.server.common.data.sms.config.SmsProviderConfiguration;

public interface SmsSenderFactory {

    SmsSender createSmsSender(SmsProviderConfiguration config);

}
