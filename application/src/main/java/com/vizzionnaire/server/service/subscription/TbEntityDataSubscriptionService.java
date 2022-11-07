package com.vizzionnaire.server.service.subscription;

import com.vizzionnaire.server.service.telemetry.TelemetryWebSocketSessionRef;
import com.vizzionnaire.server.service.telemetry.cmd.v2.AlarmDataCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityCountCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityDataCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityDataUnsubscribeCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.UnsubscribeCmd;

public interface TbEntityDataSubscriptionService {

    void handleCmd(TelemetryWebSocketSessionRef sessionId, EntityDataCmd cmd);

    void handleCmd(TelemetryWebSocketSessionRef sessionId, EntityCountCmd cmd);

    void handleCmd(TelemetryWebSocketSessionRef sessionId, AlarmDataCmd cmd);

    void cancelSubscription(String sessionId, UnsubscribeCmd subscriptionId);

    void cancelAllSessionSubscriptions(String sessionId);

}
