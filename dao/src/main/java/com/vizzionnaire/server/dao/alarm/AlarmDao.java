package com.vizzionnaire.server.dao.alarm;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.alarm.AlarmInfo;
import com.vizzionnaire.server.common.data.alarm.AlarmQuery;
import com.vizzionnaire.server.common.data.alarm.AlarmSeverity;
import com.vizzionnaire.server.common.data.alarm.AlarmStatus;
import com.vizzionnaire.server.common.data.alarm.EntityAlarm;
import com.vizzionnaire.server.common.data.id.AlarmId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.query.AlarmData;
import com.vizzionnaire.server.common.data.query.AlarmDataQuery;
import com.vizzionnaire.server.dao.Dao;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ashvayka on 11.05.17.
 */
public interface AlarmDao extends Dao<Alarm> {

    Boolean deleteAlarm(TenantId tenantId, Alarm alarm);

    ListenableFuture<Alarm> findLatestByOriginatorAndType(TenantId tenantId, EntityId originator, String type);

    ListenableFuture<Alarm> findAlarmByIdAsync(TenantId tenantId, UUID key);

    Alarm save(TenantId tenantId, Alarm alarm);

    PageData<AlarmInfo> findAlarms(TenantId tenantId, AlarmQuery query);

    PageData<AlarmInfo> findCustomerAlarms(TenantId tenantId, CustomerId customerId, AlarmQuery query);

    PageData<AlarmData> findAlarmDataByQueryForEntities(TenantId tenantId, AlarmDataQuery query, Collection<EntityId> orderedEntityIds);

    Set<AlarmSeverity> findAlarmSeverities(TenantId tenantId, EntityId entityId, Set<AlarmStatus> status);

    PageData<AlarmId> findAlarmsIdsByEndTsBeforeAndTenantId(Long time, TenantId tenantId, PageLink pageLink);

    void createEntityAlarmRecord(EntityAlarm entityAlarm);

    List<EntityAlarm> findEntityAlarmRecords(TenantId tenantId, AlarmId id);

    void deleteEntityAlarmRecords(TenantId tenantId, EntityId entityId);
}
