package com.vizzionnaire.server.transport.lwm2m.server.log;

import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;

public interface LwM2MTelemetryLogService {

    void log(LwM2mClient client, String msg);

}
