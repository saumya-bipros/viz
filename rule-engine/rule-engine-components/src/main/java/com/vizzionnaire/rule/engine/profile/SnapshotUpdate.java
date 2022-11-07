package com.vizzionnaire.rule.engine.profile;

import lombok.Getter;

import java.util.Set;

import com.vizzionnaire.server.common.data.device.profile.AlarmConditionFilterKey;
import com.vizzionnaire.server.common.data.device.profile.AlarmConditionKeyType;
import com.vizzionnaire.server.common.data.query.EntityKey;
import com.vizzionnaire.server.common.data.query.EntityKeyType;

class SnapshotUpdate {

    @Getter
    private final AlarmConditionKeyType type;
    @Getter
    private final Set<AlarmConditionFilterKey> keys;

    SnapshotUpdate(AlarmConditionKeyType type, Set<AlarmConditionFilterKey> keys) {
        this.type = type;
        this.keys = keys;
    }

    boolean hasUpdate(){
        return !keys.isEmpty();
    }
}
