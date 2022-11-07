package com.vizzionnaire.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.kv.Aggregation;
import com.vizzionnaire.server.common.data.query.EntityDataPageLink;
import com.vizzionnaire.server.common.data.query.EntityDataQuery;
import com.vizzionnaire.server.common.data.query.EntityFilter;
import com.vizzionnaire.server.common.data.query.EntityKey;
import com.vizzionnaire.server.service.telemetry.cmd.TelemetryPluginCmdsWrapper;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityCountCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityCountUpdate;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityDataCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityDataUpdate;
import com.vizzionnaire.server.service.telemetry.cmd.v2.EntityHistoryCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.LatestValueCmd;
import com.vizzionnaire.server.service.telemetry.cmd.v2.TimeSeriesCmd;

import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TbTestWebSocketClient extends WebSocketClient {

    private volatile String lastMsg;
    private volatile CountDownLatch reply;
    private volatile CountDownLatch update;

    public TbTestWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {

    }

    @Override
    public void onMessage(String s) {
        log.info("RECEIVED: {}", s);
        lastMsg = s;
        if (update != null) {
            update.countDown();
        }
        if (reply != null) {
            reply.countDown();
        }
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("CLOSED.");
    }

    @Override
    public void onError(Exception e) {
        log.error("ERROR:", e);
    }

    public void registerWaitForUpdate() {
        registerWaitForUpdate(1);
    }

    public void registerWaitForUpdate(int count) {
        lastMsg = null;
        update = new CountDownLatch(count);
    }

    @Override
    public void send(String text) throws NotYetConnectedException {
        reply = new CountDownLatch(1);
        super.send(text);
    }

    public void send(EntityDataCmd cmd) throws NotYetConnectedException {
        TelemetryPluginCmdsWrapper wrapper = new TelemetryPluginCmdsWrapper();
        wrapper.setEntityDataCmds(Collections.singletonList(cmd));
        this.send(JacksonUtil.toString(wrapper));
    }

    public void send(EntityCountCmd cmd) throws NotYetConnectedException {
        TelemetryPluginCmdsWrapper wrapper = new TelemetryPluginCmdsWrapper();
        wrapper.setEntityCountCmds(Collections.singletonList(cmd));
        this.send(JacksonUtil.toString(wrapper));
    }

    public String waitForUpdate() {
        return waitForUpdate(TimeUnit.SECONDS.toMillis(3));
    }

    public String waitForUpdate(long ms) {
        try {
            update.await(ms, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            log.warn("Failed to await reply", e);
        }
        return lastMsg;
    }

    public String waitForReply() {
        try {
            reply.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.warn("Failed to await reply", e);
        }
        return lastMsg;
    }

    public EntityDataUpdate parseDataReply(String msg) {
        return JacksonUtil.fromString(msg, EntityDataUpdate.class);
    }

    public EntityCountUpdate parseCountReply(String msg) {
        return JacksonUtil.fromString(msg, EntityCountUpdate.class);
    }

    public EntityDataUpdate subscribeLatestUpdate(List<EntityKey> keys, EntityFilter entityFilter) {
        EntityDataQuery edq = new EntityDataQuery(entityFilter, new EntityDataPageLink(1, 0, null, null),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        return subscribeLatestUpdate(keys, edq);
    }

    public EntityDataUpdate subscribeLatestUpdate(List<EntityKey> keys) {
        return subscribeLatestUpdate(keys, (EntityDataQuery) null);
    }

    public EntityDataUpdate subscribeLatestUpdate(List<EntityKey> keys, EntityDataQuery edq) {
        LatestValueCmd latestCmd = new LatestValueCmd();
        latestCmd.setKeys(keys);
        EntityDataCmd cmd = new EntityDataCmd(1, edq, null, latestCmd, null);
        send(cmd);
        return parseDataReply(waitForReply());
    }

    public EntityDataUpdate subscribeTsUpdate(List<String> keys, long startTs, long timeWindow) {
        return subscribeTsUpdate(keys, startTs, timeWindow, (EntityDataQuery) null);
    }

    public EntityDataUpdate subscribeTsUpdate(List<String> keys, long startTs, long timeWindow, EntityFilter entityFilter) {
        EntityDataQuery edq = new EntityDataQuery(entityFilter, new EntityDataPageLink(1, 0, null, null),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        return subscribeTsUpdate(keys, startTs, timeWindow, edq);
    }

    public EntityDataUpdate subscribeTsUpdate(List<String> keys, long startTs, long timeWindow, EntityDataQuery edq) {
        TimeSeriesCmd tsCmd = new TimeSeriesCmd();
        tsCmd.setKeys(keys);
        tsCmd.setAgg(Aggregation.NONE);
        tsCmd.setLimit(1000);
        tsCmd.setStartTs(startTs - timeWindow);
        tsCmd.setTimeWindow(timeWindow);

        EntityDataCmd cmd = new EntityDataCmd(1, edq, null, null, tsCmd);

        send(cmd);
        return parseDataReply(waitForReply());
    }

    public EntityDataUpdate sendHistoryCmd(List<String> keys, long startTs, long timeWindow) {
        return sendHistoryCmd(keys, startTs, timeWindow, (EntityDataQuery) null);
    }

    public EntityDataUpdate sendHistoryCmd(List<String> keys, long startTs, long timeWindow, EntityFilter entityFilter) {
        EntityDataQuery edq = new EntityDataQuery(entityFilter,
                new EntityDataPageLink(1, 0, null, null),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        return sendHistoryCmd(keys, startTs, timeWindow, edq);
    }

    public EntityDataUpdate sendHistoryCmd(List<String> keys, long startTs, long timeWindow, EntityDataQuery edq) {
        EntityHistoryCmd historyCmd = new EntityHistoryCmd();
        historyCmd.setKeys(keys);
        historyCmd.setAgg(Aggregation.NONE);
        historyCmd.setLimit(1000);
        historyCmd.setStartTs(startTs - timeWindow);
        historyCmd.setEndTs(startTs);

        EntityDataCmd cmd = new EntityDataCmd(1, edq, historyCmd, null, null);

        send(cmd);
        return parseDataReply(this.waitForReply());
    }

    public EntityDataUpdate sendEntityDataQuery(EntityDataQuery edq) {
        log.warn("sendEntityDataQuery {}", edq);
        EntityDataCmd cmd = new EntityDataCmd(1, edq, null, null, null);
        send(cmd);
        String msg = this.waitForReply();
        return parseDataReply(msg);
    }

    public EntityDataUpdate sendEntityDataQuery(EntityFilter entityFilter) {
        log.warn("sendEntityDataQuery {}", entityFilter);
        EntityDataQuery edq = new EntityDataQuery(entityFilter, new EntityDataPageLink(1, 0, null, null),
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        return sendEntityDataQuery(edq);
    }

}