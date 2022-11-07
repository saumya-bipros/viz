package com.vizzionnaire.server.common.data.query;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.alarm.AlarmInfo;
import com.vizzionnaire.server.common.data.id.EntityId;

public class AlarmData extends AlarmInfo {

    @Getter
    private final EntityId entityId;
    @Getter
    private final Map<EntityKeyType, Map<String, TsValue>> latest;

    public AlarmData(Alarm alarm, String originatorName, EntityId entityId) {
        super(alarm, originatorName);
        this.entityId = entityId;
        this.latest = new HashMap<>();
    }
}
