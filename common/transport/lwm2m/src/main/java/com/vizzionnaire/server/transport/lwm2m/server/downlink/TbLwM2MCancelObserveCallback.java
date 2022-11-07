package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import static com.vizzionnaire.server.transport.lwm2m.utils.LwM2MTransportUtil.LOG_LWM2M_INFO;

import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.log.LwM2MTelemetryLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TbLwM2MCancelObserveCallback extends AbstractTbLwM2MRequestCallback<TbLwM2MCancelObserveRequest, Integer> {

    private final String versionedId;

    public TbLwM2MCancelObserveCallback(LwM2MTelemetryLogService logService, LwM2mClient client, String versionedId) {
        super(logService, client);
        this.versionedId = versionedId;
    }

    @Override
    public void onSuccess(TbLwM2MCancelObserveRequest request, Integer canceledSubscriptionsCount) {
        log.trace("[{}] Cancel observation of [{}] successful: {}", client.getEndpoint(),  versionedId, canceledSubscriptionsCount);
        logService.log(client, String.format("[%s]: Cancel Observe for [%s] successful. Result: [%s]", LOG_LWM2M_INFO, versionedId, canceledSubscriptionsCount));
    }

}
