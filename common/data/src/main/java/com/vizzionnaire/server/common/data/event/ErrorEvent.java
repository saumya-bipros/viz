package com.vizzionnaire.server.common.data.event;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.EventInfo;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@ToString
@EqualsAndHashCode(callSuper = true)
public class ErrorEvent extends Event {

    private static final long serialVersionUID = 960461434033192571L;

    @Builder
    private ErrorEvent(TenantId tenantId, UUID entityId, String serviceId, UUID id, long ts, String method, String error) {
        super(tenantId, entityId, serviceId, id, ts);
        this.method = method;
        this.error = error;
    }

    @Getter
    @Setter
    private String method;
    @Getter
    @Setter
    private String error;

    @Override
    public EventType getType() {
        return EventType.ERROR;
    }

    @Override
    public EventInfo toInfo(EntityType entityType) {
        EventInfo eventInfo = super.toInfo(entityType);
        var json = (ObjectNode) eventInfo.getBody();
        json.put("method", method);
        if (error != null) {
            json.put("error", error);
        }
        return eventInfo;
    }
}
