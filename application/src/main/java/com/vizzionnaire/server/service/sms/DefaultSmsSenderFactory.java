package com.vizzionnaire.server.service.sms;

import org.springframework.stereotype.Component;

import com.vizzionnaire.rule.engine.api.sms.SmsSender;
import com.vizzionnaire.rule.engine.api.sms.SmsSenderFactory;
import com.vizzionnaire.server.common.data.sms.config.AwsSnsSmsProviderConfiguration;
import com.vizzionnaire.server.common.data.sms.config.SmppSmsProviderConfiguration;
import com.vizzionnaire.server.common.data.sms.config.SmsProviderConfiguration;
import com.vizzionnaire.server.common.data.sms.config.TwilioSmsProviderConfiguration;
import com.vizzionnaire.server.service.sms.aws.AwsSmsSender;
import com.vizzionnaire.server.service.sms.smpp.SmppSmsSender;
import com.vizzionnaire.server.service.sms.twilio.TwilioSmsSender;

@Component
public class DefaultSmsSenderFactory implements SmsSenderFactory {

    @Override
    public SmsSender createSmsSender(SmsProviderConfiguration config) {
        switch (config.getType()) {
            case AWS_SNS:
                return new AwsSmsSender((AwsSnsSmsProviderConfiguration)config);
            case TWILIO:
                return new TwilioSmsSender((TwilioSmsProviderConfiguration)config);
            case SMPP:
                return new SmppSmsSender((SmppSmsProviderConfiguration) config);
            default:
                throw new RuntimeException("Unknown SMS provider type " + config.getType());
        }
    }

}
