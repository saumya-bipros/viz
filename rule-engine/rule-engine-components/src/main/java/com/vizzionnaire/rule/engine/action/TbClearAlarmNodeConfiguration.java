package com.vizzionnaire.rule.engine.action;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.server.common.data.alarm.AlarmSeverity;

@Data
public class TbClearAlarmNodeConfiguration extends TbAbstractAlarmNodeConfiguration implements NodeConfiguration<TbClearAlarmNodeConfiguration> {

    @Override
    public TbClearAlarmNodeConfiguration defaultConfiguration() {
        TbClearAlarmNodeConfiguration configuration = new TbClearAlarmNodeConfiguration();
        configuration.setAlarmDetailsBuildJs(ALARM_DETAILS_BUILD_JS_TEMPLATE);
        configuration.setAlarmType("General Alarm");
        return configuration;
    }
}
