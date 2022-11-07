package com.vizzionnaire.server.dao.model.sql;

import com.vizzionnaire.server.common.data.alarm.AlarmInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlarmInfoEntity extends AbstractAlarmEntity<AlarmInfo> {

    private String originatorName;

    public AlarmInfoEntity() {
        super();
    }

    public AlarmInfoEntity(AlarmEntity alarmEntity) {
        super(alarmEntity);
    }

    @Override
    public AlarmInfo toData() {
        return new AlarmInfo(super.toAlarm(), this.originatorName);
    }
}
