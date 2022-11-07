package com.vizzionnaire.server.common.data.query;

import lombok.Data;

import java.util.Map;

import com.vizzionnaire.server.common.data.id.EntityId;

@Data
public class EntityData {

    private final EntityId entityId;
    private final Map<EntityKeyType, Map<String, TsValue>> latest;
    private final Map<String, TsValue[]> timeseries;

}
