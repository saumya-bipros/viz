package com.vizzionnaire.rule.engine.api;

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
import com.vizzionnaire.server.dao.alarm.AlarmOperationResult;

import java.util.Collection;

/**
 * Created by ashvayka on 02.04.18.
 */
public interface RuleEngineAlarmService {

    Alarm createOrUpdateAlarm(Alarm alarm);

    Boolean deleteAlarm(TenantId tenantId, AlarmId alarmId);

    ListenableFuture<Boolean> ackAlarm(TenantId tenantId, AlarmId alarmId, long ackTs);

    ListenableFuture<Boolean> clearAlarm(TenantId tenantId, AlarmId alarmId, JsonNode details, long clearTs);

    ListenableFuture<AlarmOperationResult> clearAlarmForResult(TenantId tenantId, AlarmId alarmId, JsonNode details, long clearTs);

    ListenableFuture<Alarm> findAlarmByIdAsync(TenantId tenantId, AlarmId alarmId);

    ListenableFuture<Alarm> findLatestByOriginatorAndType(TenantId tenantId, EntityId originator, String type);

    ListenableFuture<AlarmInfo> findAlarmInfoByIdAsync(TenantId tenantId, AlarmId alarmId);

    ListenableFuture<PageData<AlarmInfo>> findAlarms(TenantId tenantId, AlarmQuery query);

    ListenableFuture<PageData<AlarmInfo>> findCustomerAlarms(TenantId tenantId, CustomerId customerId, AlarmQuery query);

    AlarmSeverity findHighestAlarmSeverity(TenantId tenantId, EntityId entityId, AlarmSearchStatus alarmSearchStatus, AlarmStatus alarmStatus);

    PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId, AlarmDataQuery query, Collection<EntityId> orderedEntityIds);
}
