package com.vizzionnaire.server.common.data.device.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vizzionnaire.server.common.data.query.FilterPredicateValue;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DurationAlarmConditionSpec implements AlarmConditionSpec {

    private TimeUnit unit;
    private FilterPredicateValue<Long> predicate;

    @Override
    public AlarmConditionSpecType getType() {
        return AlarmConditionSpecType.DURATION;
    }
}
