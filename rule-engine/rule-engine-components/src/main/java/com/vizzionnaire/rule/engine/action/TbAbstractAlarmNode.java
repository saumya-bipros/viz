package com.vizzionnaire.rule.engine.action;

import static com.vizzionnaire.common.util.DonAsynchron.withCallback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.rule.engine.api.ScriptEngine;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.server.common.data.DataConstants;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class TbAbstractAlarmNode<C extends TbAbstractAlarmNodeConfiguration> implements TbNode {

    static final String PREV_ALARM_DETAILS = "prevAlarmDetails";

    private final ObjectMapper mapper = new ObjectMapper();

    protected C config;
    private ScriptEngine buildDetailsJsEngine;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = loadAlarmNodeConfig(configuration);
        this.buildDetailsJsEngine = ctx.createJsScriptEngine(config.getAlarmDetailsBuildJs());
    }

    protected abstract C loadAlarmNodeConfig(TbNodeConfiguration configuration) throws TbNodeException;

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        withCallback(processAlarm(ctx, msg),
                alarmResult -> {
                    if (alarmResult.alarm == null) {
                        ctx.tellNext(msg, "False");
                    } else if (alarmResult.isCreated) {
                        tellNext(ctx, msg, alarmResult, DataConstants.ENTITY_CREATED, "Created");
                    } else if (alarmResult.isUpdated) {
                        tellNext(ctx, msg, alarmResult, DataConstants.ENTITY_UPDATED, "Updated");
                    } else if (alarmResult.isCleared) {
                        tellNext(ctx, msg, alarmResult, DataConstants.ALARM_CLEAR, "Cleared");
                    } else {
                        ctx.tellSuccess(msg);
                    }
                },
                t -> ctx.tellFailure(msg, t), ctx.getDbCallbackExecutor());
    }

    protected abstract ListenableFuture<TbAlarmResult> processAlarm(TbContext ctx, TbMsg msg);

    protected ListenableFuture<JsonNode> buildAlarmDetails(TbContext ctx, TbMsg msg, JsonNode previousDetails) {
        try {
            TbMsg dummyMsg = msg;
            if (previousDetails != null) {
                TbMsgMetaData metaData = msg.getMetaData().copy();
                metaData.putValue(PREV_ALARM_DETAILS, mapper.writeValueAsString(previousDetails));
                dummyMsg = ctx.transformMsg(msg, msg.getType(), msg.getOriginator(), metaData, msg.getData());
            }
            return buildDetailsJsEngine.executeJsonAsync(dummyMsg);
        } catch (Exception e) {
            return Futures.immediateFailedFuture(e);
        }
    }

    public static TbMsg toAlarmMsg(TbContext ctx, TbAlarmResult alarmResult, TbMsg originalMsg) {
        JsonNode jsonNodes = JacksonUtil.valueToTree(alarmResult.alarm);
        String data = jsonNodes.toString();
        TbMsgMetaData metaData = originalMsg.getMetaData().copy();
        if (alarmResult.isCreated) {
            metaData.putValue(DataConstants.IS_NEW_ALARM, Boolean.TRUE.toString());
        } else if (alarmResult.isUpdated) {
            metaData.putValue(DataConstants.IS_EXISTING_ALARM, Boolean.TRUE.toString());
        } else if (alarmResult.isCleared) {
            metaData.putValue(DataConstants.IS_CLEARED_ALARM, Boolean.TRUE.toString());
        }
        return ctx.transformMsg(originalMsg, "ALARM", originalMsg.getOriginator(), metaData, data);
    }

    @Override
    public void destroy() {
        if (buildDetailsJsEngine != null) {
            buildDetailsJsEngine.destroy();
        }
    }

    private void tellNext(TbContext ctx, TbMsg msg, TbAlarmResult alarmResult, String entityAction, String alarmAction) {
        ctx.enqueue(ctx.alarmActionMsg(alarmResult.alarm, ctx.getSelfId(), entityAction),
                () -> ctx.tellNext(toAlarmMsg(ctx, alarmResult, msg), alarmAction),
                throwable -> ctx.tellFailure(toAlarmMsg(ctx, alarmResult, msg), throwable));
    }
}
