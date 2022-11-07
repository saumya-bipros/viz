package com.vizzionnaire.server.common.data.rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.vizzionnaire.server.common.data.id.RuleNodeId;

import lombok.Data;

import java.util.Map;

/**
 * Created by igor on 3/13/18.
 */
@Data
public class RuleNodeUpdateResult {

    private final RuleNode oldRuleNode;
    private final RuleNode newRuleNode;

}
