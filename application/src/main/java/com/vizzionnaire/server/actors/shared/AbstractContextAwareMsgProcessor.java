package com.vizzionnaire.server.actors.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActorCtx;
import com.vizzionnaire.server.common.msg.TbActorMsg;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public abstract class AbstractContextAwareMsgProcessor {

    protected final static ObjectMapper mapper = new ObjectMapper();

    protected final ActorSystemContext systemContext;

    protected AbstractContextAwareMsgProcessor(ActorSystemContext systemContext) {
        super();
        this.systemContext = systemContext;
    }

    private ScheduledExecutorService getScheduler() {
        return systemContext.getScheduler();
    }

    protected void schedulePeriodicMsgWithDelay(TbActorCtx ctx, TbActorMsg msg, long delayInMs, long periodInMs) {
        systemContext.schedulePeriodicMsgWithDelay(ctx, msg, delayInMs, periodInMs);
    }

    protected void scheduleMsgWithDelay(TbActorCtx ctx, TbActorMsg msg, long delayInMs) {
        systemContext.scheduleMsgWithDelay(ctx, msg, delayInMs);
    }

}
