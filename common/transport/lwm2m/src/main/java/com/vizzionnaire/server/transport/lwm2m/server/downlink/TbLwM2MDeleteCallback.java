package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import org.eclipse.leshan.core.request.DeleteRequest;
import org.eclipse.leshan.core.response.DeleteResponse;

import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.log.LwM2MTelemetryLogService;

public class TbLwM2MDeleteCallback extends TbLwM2MTargetedCallback<DeleteRequest, DeleteResponse> {

    public TbLwM2MDeleteCallback(LwM2MTelemetryLogService logService, LwM2mClient client, String targetId) {
        super(logService, client, targetId);
    }

}
