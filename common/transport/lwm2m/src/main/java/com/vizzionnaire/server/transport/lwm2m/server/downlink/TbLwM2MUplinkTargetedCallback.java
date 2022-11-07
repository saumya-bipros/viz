package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;
import com.vizzionnaire.server.transport.lwm2m.server.log.LwM2MTelemetryLogService;
import com.vizzionnaire.server.transport.lwm2m.server.uplink.LwM2mUplinkMsgHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TbLwM2MUplinkTargetedCallback<R, T> extends TbLwM2MTargetedCallback<R, T> {

    protected LwM2mUplinkMsgHandler handler;

    public TbLwM2MUplinkTargetedCallback(LwM2mUplinkMsgHandler handler, LwM2MTelemetryLogService logService, LwM2mClient client, String versionedId) {
        super(logService, client, versionedId);
        this.handler = handler;
    }

    public TbLwM2MUplinkTargetedCallback(LwM2mUplinkMsgHandler handler, LwM2MTelemetryLogService logService, LwM2mClient client, String[] versionedIds) {
        super(logService, client, versionedIds);
        this.handler = handler;
    }

}
