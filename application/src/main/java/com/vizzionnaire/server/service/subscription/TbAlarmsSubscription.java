package com.vizzionnaire.server.service.subscription;

import lombok.Builder;
import lombok.Getter;

import com.vizzionnaire.server.common.data.alarm.AlarmSearchStatus;
import com.vizzionnaire.server.common.data.alarm.AlarmSeverity;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.service.telemetry.sub.AlarmSubscriptionUpdate;

import java.util.List;
import java.util.function.BiConsumer;

public class TbAlarmsSubscription extends TbSubscription<AlarmSubscriptionUpdate> {

    @Getter
    private final long ts;

    @Builder
    public TbAlarmsSubscription(String serviceId, String sessionId, int subscriptionId, TenantId tenantId, EntityId entityId,
                                BiConsumer<String, AlarmSubscriptionUpdate> updateConsumer, long ts) {
        super(serviceId, sessionId, subscriptionId, tenantId, entityId, TbSubscriptionType.ALARMS, updateConsumer);
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
