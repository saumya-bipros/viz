package com.vizzionnaire.rule.engine.data;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;

@Data
public class DeviceRelationsQuery {
    private EntitySearchDirection direction;
    private int maxLevel = 1;
    private String relationType;
    private List<String> deviceTypes;
    private boolean fetchLastLevelOnly;
}
