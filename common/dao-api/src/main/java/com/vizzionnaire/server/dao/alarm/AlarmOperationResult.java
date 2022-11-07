package com.vizzionnaire.server.dao.alarm;

import lombok.Data;

import java.util.Collections;
import java.util.List;

import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.id.EntityId;

@Data
public class AlarmOperationResult {
    private final Alarm alarm;
    private final boolean successful;
    private final boolean created;
    private final List<EntityId> propagatedEntitiesList;

    public AlarmOperationResult(Alarm alarm, boolean successful) {
        this(alarm, successful, Collections.emptyList());
    }

    public AlarmOperationResult(Alarm alarm, boolean successful, List<EntityId> propagatedEntitiesList) {
        this(alarm, successful, false, propagatedEntitiesList);
    }

    public AlarmOperationResult(Alarm alarm, boolean successful, boolean created, List<EntityId> propagatedEntitiesList) {
        this.alarm = alarm;
        this.successful = successful;
        this.created = created;
        this.propagatedEntitiesList = propagatedEntitiesList;
    }
}
