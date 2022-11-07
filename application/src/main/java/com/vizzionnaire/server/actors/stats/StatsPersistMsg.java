package com.vizzionnaire.server.actors.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;

@AllArgsConstructor
@Getter
@ToString
public final class StatsPersistMsg implements TbActorMsg {

    private final long messagesProcessed;
    private final long errorsOccurred;
    private final TenantId tenantId;
    private final EntityId entityId;

    @Override
    public MsgType getMsgType() {
        return MsgType.STATS_PERSIST_MSG;
    }

    public boolean isEmpty() {
        return messagesProcessed == 0 && errorsOccurred == 0;
    }

}
