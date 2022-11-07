package com.vizzionnaire.server.service.edge.rpc;

import com.google.common.util.concurrent.SettableFuture;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Data
public class EdgeSessionState {

    private final Map<Integer, DownlinkMsg> pendingMsgsMap = new LinkedHashMap<>();
    private SettableFuture<Void> sendDownlinkMsgsFuture;
    private ScheduledFuture<?> scheduledSendDownlinkTask;
}
