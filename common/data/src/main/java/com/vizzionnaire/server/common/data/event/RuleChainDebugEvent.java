package com.vizzionnaire.server.common.data.event;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.EventInfo;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = true)
public class RuleChainDebugEvent extends Event {

    private static final long serialVersionUID = -386392236201116767L;

    @Builder
    private RuleChainDebugEvent(TenantId tenantId, UUID entityId, String serviceId, UUID id, long ts, String message, String error) {
        super(tenantId, entityId, serviceId, id, ts);
        this.message = message;
        this.error = error;
    }

    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private String error;

    @Override
    public EventType getType() {
        return EventType.DEBUG_RULE_CHAIN;
    }

    @Override
    public EventInfo toInfo(EntityType entityType) {
        EventInfo eventInfo = super.toInfo(entityType);
        var json = (ObjectNode) eventInfo.getBody();
        putNotNull(json, "message", message);
        putNotNull(json, "error", error);
        return eventInfo;
    }
}
