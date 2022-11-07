package com.vizzionnaire.server.common.data.device.profile;

import lombok.Data;

import java.util.Set;

import com.vizzionnaire.server.common.data.query.DynamicValue;

@Data
public class SpecificTimeSchedule implements AlarmSchedule {

    private String timezone;
    private Set<Integer> daysOfWeek;
    private long startsOn;
    private long endsOn;

    private DynamicValue<String> dynamicValue;

    @Override
    public AlarmScheduleType getType() {
        return AlarmScheduleType.SPECIFIC_TIME;
    }

}
