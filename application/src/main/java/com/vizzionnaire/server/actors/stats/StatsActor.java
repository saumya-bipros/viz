package com.vizzionnaire.server.actors.stats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActor;
import com.vizzionnaire.server.actors.TbActorId;
import com.vizzionnaire.server.actors.TbStringActorId;
import com.vizzionnaire.server.actors.service.ContextAwareActor;
import com.vizzionnaire.server.actors.service.ContextBasedCreator;
import com.vizzionnaire.server.common.data.DataConstants;
import com.vizzionnaire.server.common.data.EventInfo;
import com.vizzionnaire.server.common.data.event.StatisticsEvent;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatsActor extends ContextAwareActor {

    private final ObjectMapper mapper = new ObjectMapper();

    public StatsActor(ActorSystemContext context) {
        super(context);
    }

    @Override
    protected boolean doProcess(TbActorMsg msg) {
        log.debug("Received message: {}", msg);
        if (msg.getMsgType().equals(MsgType.STATS_PERSIST_MSG)) {
            onStatsPersistMsg((StatsPersistMsg) msg);
            return true;
        } else {
            return false;
        }
    }

    public void onStatsPersistMsg(StatsPersistMsg msg) {
        if (msg.isEmpty()) {
            return;
        }
        systemContext.getEventService().saveAsync(StatisticsEvent.builder()
                .tenantId(msg.getTenantId())
                .entityId(msg.getEntityId().getId())
                .serviceId(systemContext.getServiceInfoProvider().getServiceId())
                .messagesProcessed(msg.getMessagesProcessed())
                .errorsOccurred(msg.getErrorsOccurred())
                .build()
        );
    }

    private JsonNode toBodyJson(String serviceId, long messagesProcessed, long errorsOccurred) {
        return mapper.createObjectNode().put("server", serviceId).put("messagesProcessed", messagesProcessed).put("errorsOccurred", errorsOccurred);
    }

    public static class ActorCreator extends ContextBasedCreator {
        private final String actorId;

        public ActorCreator(ActorSystemContext context, String actorId) {
            super(context);
            this.actorId = actorId;
        }

        @Override
        public TbActorId createActorId() {
            return new TbStringActorId(actorId);
        }

        @Override
        public TbActor createActor() {
            return new StatsActor(context);
        }
    }
}
