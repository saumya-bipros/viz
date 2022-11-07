package com.vizzionnaire.rule.engine.profile.state;

import lombok.Data;

import java.util.Map;

import com.vizzionnaire.server.common.data.alarm.AlarmSeverity;

@Data
public class PersistedAlarmState {

    private Map<AlarmSeverity, PersistedAlarmRuleState> createRuleStates;
    private PersistedAlarmRuleState clearRuleState;

}
