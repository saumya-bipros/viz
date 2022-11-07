package com.vizzionnaire.server.common.msg.plugin;

import lombok.ToString;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.common.msg.MsgType;

import java.util.Optional;

/**
 * @author Andrew Shvayka
 */
@ToString
public class RuleNodeUpdatedMsg extends ComponentLifecycleMsg {

    public RuleNodeUpdatedMsg(TenantId tenantId, EntityId entityId) {
        super(tenantId, entityId, ComponentLifecycleEvent.UPDATED);
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.RULE_NODE_UPDATED_MSG;
    }
}