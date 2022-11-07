package com.vizzionnaire.server.common.data.alarm;

import com.vizzionnaire.server.common.data.HasTenantId;
import com.vizzionnaire.server.common.data.id.AlarmId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EntityAlarm implements HasTenantId {

    private TenantId tenantId;
    private EntityId entityId;
    private long createdTime;
    private String alarmType;

    private CustomerId customerId;
    private AlarmId alarmId;

}
