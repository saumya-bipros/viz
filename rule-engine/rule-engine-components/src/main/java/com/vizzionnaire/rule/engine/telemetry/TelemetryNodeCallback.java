package com.vizzionnaire.rule.engine.telemetry;

import com.google.common.util.concurrent.FutureCallback;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.server.common.msg.TbMsg;

import lombok.Data;

import javax.annotation.Nullable;

/**
 * Created by ashvayka on 02.04.18.
 */
@Data
class TelemetryNodeCallback implements FutureCallback<Void> {
    private final TbContext ctx;
    private final TbMsg msg;

    @Override
    public void onSuccess(@Nullable Void result) {
        ctx.tellSuccess(msg);
    }

    @Override
    public void onFailure(Throwable t) {
        ctx.tellFailure(msg, t);
    }
}
