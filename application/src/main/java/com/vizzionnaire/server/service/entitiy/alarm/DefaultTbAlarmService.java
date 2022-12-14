package com.vizzionnaire.server.service.entitiy.alarm;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.alarm.AlarmStatus;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.alarm.AlarmOperationResult;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DefaultTbAlarmService extends AbstractTbEntityService implements TbAlarmService {

    @Override
    public Alarm save(Alarm alarm, User user) throws VizzionnaireException {
        ActionType actionType = alarm.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = alarm.getTenantId();
        try {
            Alarm savedAlarm = checkNotNull(alarmService.createOrUpdateAlarm(alarm).getAlarm());
            notificationEntityService.notifyCreateOrUpdateAlarm(savedAlarm, actionType, user);
            return savedAlarm;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.ALARM), alarm, actionType, user, e);
            throw e;
        }
    }

    @Override
    public ListenableFuture<Void> ack(Alarm alarm, User user) {
        long ackTs = System.currentTimeMillis();
        ListenableFuture<AlarmOperationResult> future = alarmService.ackAlarm(alarm.getTenantId(), alarm.getId(), ackTs);
        return Futures.transform(future, result -> {
            alarm.setAckTs(ackTs);
            alarm.setStatus(alarm.getStatus().isCleared() ? AlarmStatus.CLEARED_ACK : AlarmStatus.ACTIVE_ACK);
            notificationEntityService.notifyCreateOrUpdateAlarm(alarm, ActionType.ALARM_ACK, user);
            return null;
        }, MoreExecutors.directExecutor());
    }

    @Override
    public ListenableFuture<Void> clear(Alarm alarm, User user) {
        long clearTs = System.currentTimeMillis();
        ListenableFuture<AlarmOperationResult> future = alarmService.clearAlarm(alarm.getTenantId(), alarm.getId(), null, clearTs);
        return Futures.transform(future, result -> {
            alarm.setClearTs(clearTs);
            alarm.setStatus(alarm.getStatus().isAck() ? AlarmStatus.CLEARED_ACK : AlarmStatus.CLEARED_UNACK);
            notificationEntityService.notifyCreateOrUpdateAlarm(alarm, ActionType.ALARM_CLEAR, user);
            return null;
        }, MoreExecutors.directExecutor());
    }

    @Override
    public Boolean delete(Alarm alarm, User user) {
        TenantId tenantId = alarm.getTenantId();
        List<EdgeId> relatedEdgeIds = findRelatedEdgeIds(tenantId, alarm.getOriginator());
        notificationEntityService.notifyDeleteAlarm(tenantId, alarm, alarm.getOriginator(), alarm.getCustomerId(),
                relatedEdgeIds, user, JacksonUtil.toString(alarm));
        return alarmService.deleteAlarm(tenantId, alarm.getId()).isSuccessful();
    }
}