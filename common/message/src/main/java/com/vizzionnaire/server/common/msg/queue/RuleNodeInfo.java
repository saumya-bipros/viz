package com.vizzionnaire.server.common.msg.queue;

import com.vizzionnaire.server.common.data.id.RuleNodeId;

import lombok.Getter;

public class RuleNodeInfo {
    private final String label;
    @Getter
    private final RuleNodeId ruleNodeId;

    public RuleNodeInfo(RuleNodeId id, String ruleChainName, String ruleNodeName) {
        this.ruleNodeId = id;
        this.label = "[RuleChain: " + ruleChainName + "|RuleNode: " + ruleNodeName + "(" + id + ")]";
    }

    @Override
    public String toString() {
        return label;
    }
}