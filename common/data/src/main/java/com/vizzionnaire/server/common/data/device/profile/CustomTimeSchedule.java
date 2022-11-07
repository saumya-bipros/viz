package com.vizzionnaire.server.common.data.device.profile;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.common.data.query.DynamicValue;

@Data
public class CustomTimeSchedule implements AlarmSchedule {

    private String timezone;
    private List<CustomTimeScheduleItem> items;

    private DynamicValue<String> dynamicValue;

    @Override
    public AlarmScheduleType getType() {
        return AlarmScheduleType.CUSTOM;
    }

}
