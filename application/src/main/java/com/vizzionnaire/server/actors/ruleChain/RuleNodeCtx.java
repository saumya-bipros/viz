package com.vizzionnaire.server.actors.ruleChain;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.vizzionnaire.server.actors.TbActorRef;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rule.RuleNode;

/**
 * Created by ashvayka on 19.03.18.
 */
@Data
@AllArgsConstructor
final class RuleNodeCtx {
    private final TenantId tenantId;
    private final TbActorRef chainActor;
    private final TbActorRef selfActor;
    private RuleNode self;
}
