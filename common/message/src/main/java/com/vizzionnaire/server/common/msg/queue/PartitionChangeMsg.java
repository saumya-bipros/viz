package com.vizzionnaire.server.common.msg.queue;

import lombok.Data;
import lombok.Getter;

import java.util.Set;

import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;

/**
 * @author Andrew Shvayka
 */
@Data
public final class PartitionChangeMsg implements TbActorMsg {

    @Getter
    private final ServiceType serviceType;
    @Getter
    private final Set<TopicPartitionInfo> partitions;

    @Override
    public MsgType getMsgType() {
        return MsgType.PARTITION_CHANGE_MSG;
    }
}
