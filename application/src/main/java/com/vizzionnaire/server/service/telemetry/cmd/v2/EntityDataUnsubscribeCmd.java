package com.vizzionnaire.server.service.telemetry.cmd.v2;

import lombok.Data;

@Data
public class EntityDataUnsubscribeCmd implements UnsubscribeCmd {

    private final int cmdId;

}
