package com.vizzionnaire.server.common.data.rule;

import com.vizzionnaire.server.common.data.BaseData;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.RuleNodeStateId;

import lombok.Data;

@Data
public class RuleNodeState extends BaseData<RuleNodeStateId> {

    private RuleNodeId ruleNodeId;
    private EntityId entityId;
    private String stateData;

    public RuleNodeState() {
        super();
    }

    public RuleNodeState(RuleNodeStateId id) {
        super(id);
    }

    public RuleNodeState(RuleNodeState event) {
        super(event);
    }
}
