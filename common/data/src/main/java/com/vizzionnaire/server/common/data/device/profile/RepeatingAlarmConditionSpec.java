package com.vizzionnaire.server.common.data.device.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vizzionnaire.server.common.data.query.FilterPredicateValue;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RepeatingAlarmConditionSpec implements AlarmConditionSpec {

    private FilterPredicateValue<Integer> predicate;

    @Override
    public AlarmConditionSpecType getType() {
        return AlarmConditionSpecType.REPEATING;
    }
}
