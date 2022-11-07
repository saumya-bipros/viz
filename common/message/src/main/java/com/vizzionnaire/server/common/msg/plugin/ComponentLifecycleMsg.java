package com.vizzionnaire.server.common.msg.plugin;

import lombok.Getter;
import lombok.ToString;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;
import com.vizzionnaire.server.common.msg.aware.TenantAwareMsg;
import com.vizzionnaire.server.common.msg.cluster.ToAllNodesMsg;

import java.util.Optional;

/**
 * @author Andrew Shvayka
 */
@ToString
public class ComponentLifecycleMsg implements TenantAwareMsg, ToAllNodesMsg {
    @Getter
    private final TenantId tenantId;
    @Getter
    private final EntityId entityId;
    @Getter
    private final ComponentLifecycleEvent event;

    public ComponentLifecycleMsg(TenantId tenantId, EntityId entityId, ComponentLifecycleEvent event) {
        this.tenantId = tenantId;
        this.entityId = entityId;
        this.event = event;
    }

    public Optional<RuleChainId> getRuleChainId() {
        return entityId.getEntityType() == EntityType.RULE_CHAIN ? Optional.of((RuleChainId) entityId) : Optional.empty();
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.COMPONENT_LIFE_CYCLE_MSG;
    }
}
