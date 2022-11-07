package com.vizzionnaire.rule.engine.action;

import com.vizzionnaire.server.common.data.alarm.Alarm;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TbAlarmResult {
    boolean isCreated;
    boolean isUpdated;
    boolean isSeverityUpdated;
    boolean isCleared;
    Alarm alarm;

    public TbAlarmResult(boolean isCreated, boolean isUpdated, boolean isCleared, Alarm alarm) {
        this.isCreated = isCreated;
        this.isUpdated = isUpdated;
        this.isCleared = isCleared;
        this.alarm = alarm;
    }
}
