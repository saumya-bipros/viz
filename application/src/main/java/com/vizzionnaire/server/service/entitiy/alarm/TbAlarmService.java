package com.vizzionnaire.server.service.entitiy.alarm;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;

public interface TbAlarmService {

    Alarm save(Alarm entity, User user) throws ThingsboardException;

    ListenableFuture<Void> ack(Alarm alarm, User user);

    ListenableFuture<Void> clear(Alarm alarm, User user);

    Boolean delete(Alarm alarm, User user);
}
