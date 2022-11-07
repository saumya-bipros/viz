package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import static com.vizzionnaire.server.transport.lwm2m.utils.LwM2MTransportUtil.LOG_LWM2M_INFO;

import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.log.LwM2MTelemetryLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TbLwM2MCancelAllObserveCallback extends AbstractTbLwM2MRequestCallback<TbLwM2MCancelAllRequest, Integer> {

    public TbLwM2MCancelAllObserveCallback(LwM2MTelemetryLogService logService, LwM2mClient client) {
        super(logService, client);
    }

    @Override
    public void onSuccess(TbLwM2MCancelAllRequest request, Integer canceledSubscriptionsCount) {
        log.trace("[{}] Cancel of all observations was successful: {}", client.getEndpoint(),  canceledSubscriptionsCount);
        logService.log(client, String.format("[%s]: Cancel of all observations was successful. Result: [%s]", LOG_LWM2M_INFO, canceledSubscriptionsCount));
    }

}
