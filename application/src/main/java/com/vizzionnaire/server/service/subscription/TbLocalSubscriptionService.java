package com.vizzionnaire.server.service.subscription;

import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.queue.discovery.event.ClusterTopologyChangeEvent;
import com.vizzionnaire.server.queue.discovery.event.PartitionChangeEvent;
import com.vizzionnaire.server.service.telemetry.sub.AlarmSubscriptionUpdate;
import com.vizzionnaire.server.service.telemetry.sub.TelemetrySubscriptionUpdate;

public interface TbLocalSubscriptionService {

    void addSubscription(TbSubscription subscription);

    void cancelSubscription(String sessionId, int subscriptionId);

    void cancelAllSessionSubscriptions(String sessionId);

    void onSubscriptionUpdate(String sessionId, TelemetrySubscriptionUpdate update, TbCallback callback);

    void onSubscriptionUpdate(String sessionId, AlarmSubscriptionUpdate update, TbCallback callback);

    void onApplicationEvent(PartitionChangeEvent event);

    void onApplicationEvent(ClusterTopologyChangeEvent event);
}
