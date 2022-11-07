package com.vizzionnaire.server.dao.model.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;

import com.vizzionnaire.server.common.data.alarm.EntityAlarm;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EntityAlarmCompositeKey implements Serializable {

    @Transient
    private static final long serialVersionUID = -245388185894468450L;

    private UUID entityId;
    private UUID alarmId;

    public EntityAlarmCompositeKey(EntityAlarm entityAlarm) {
        this.entityId = entityAlarm.getEntityId().getId();
        this.alarmId = entityAlarm.getAlarmId().getId();
    }
}
