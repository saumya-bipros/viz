package com.vizzionnaire.server.dao.alarm;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.alarm.AlarmInfo;
import com.vizzionnaire.server.common.data.alarm.AlarmQuery;
import com.vizzionnaire.server.common.data.alarm.AlarmSearchStatus;
import com.vizzionnaire.server.common.data.alarm.AlarmSeverity;
import com.vizzionnaire.server.common.data.alarm.AlarmStatus;
import com.vizzionnaire.server.common.data.id.AlarmId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.query.AlarmData;
import com.vizzionnaire.server.common.data.query.AlarmDataQuery;

import java.util.Collection;

/**
 * Created by ashvayka on 11.05.17.
 */
public interface AlarmService {

    AlarmOperationResult createOrUpdateAlarm(Alarm alarm);

    AlarmOperationResult createOrUpdateAlarm(Alarm alarm, boolean alarmCreationEnabled);

    AlarmOperationResult deleteAlarm(TenantId tenantId, AlarmId alarmId);

    ListenableFuture<AlarmOperationResult> ackAlarm(TenantId tenantId, AlarmId alarmId, long ackTs);

    ListenableFuture<AlarmOperationResult> clearAlarm(TenantId tenantId, AlarmId alarmId, JsonNode details, long clearTs);

    ListenableFuture<Alarm> findAlarmByIdAsync(TenantId tenantId, AlarmId alarmId);

    ListenableFuture<AlarmInfo> findAlarmInfoByIdAsync(TenantId tenantId, AlarmId alarmId);

    ListenableFuture<PageData<AlarmInfo>> findAlarms(TenantId tenantId, AlarmQuery query);

    ListenableFuture<PageData<AlarmInfo>> findCustomerAlarms(TenantId tenantId, CustomerId customerId, AlarmQuery query);

    AlarmSeverity findHighestAlarmSeverity(TenantId tenantId, EntityId entityId, AlarmSearchStatus alarmSearchStatus,
                                           AlarmStatus alarmStatus);

    ListenableFuture<Alarm> findLatestByOriginatorAndType(TenantId tenantId, EntityId originator, String type);

    PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId,
                                                        AlarmDataQuery query, Collection<EntityId> orderedEntityIds);

    void deleteEntityAlarmRelations(TenantId tenantId, EntityId entityId);
}
