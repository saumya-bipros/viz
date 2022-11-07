package com.vizzionnaire.server.transport.lwm2m.server.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.vizzionnaire.server.transport.lwm2m.utils.LwM2MTransportUtil.LOG_LWM2M_TELEMETRY;

import org.springframework.stereotype.Service;

import com.vizzionnaire.server.queue.util.TbLwM2mTransportComponent;
import com.vizzionnaire.server.transport.lwm2m.server.LwM2mTransportServerHelper;
import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;

@Slf4j
@Service
@TbLwM2mTransportComponent
@RequiredArgsConstructor
public class DefaultLwM2MTelemetryLogService implements LwM2MTelemetryLogService {

    private final LwM2mTransportServerHelper helper;

    @Override
    public void log(LwM2mClient client, String logMsg) {
        if (logMsg != null && client != null && client.getSession() != null) {
            if (logMsg.length() > 1024) {
                logMsg = logMsg.substring(0, 1024);
            }
            this.helper.sendParametersOnVizzionnaireTelemetry(this.helper.getKvStringtoVizzionnaire(LOG_LWM2M_TELEMETRY, logMsg), client.getSession(), client.getKeyTsLatestMap());
        }
    }

}
