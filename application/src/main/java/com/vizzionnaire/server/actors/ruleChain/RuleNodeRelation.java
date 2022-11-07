package com.vizzionnaire.server.actors.ruleChain;

import com.vizzionnaire.server.common.data.id.EntityId;

import lombok.Data;

/**
 * Created by ashvayka on 19.03.18.
 */

@Data
final class RuleNodeRelation {

    private final EntityId in;
    private final EntityId out;
    private final String type;

}
