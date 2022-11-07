package com.vizzionnaire.server.common.msg.timeout;

import com.vizzionnaire.server.common.msg.TbActorMsg;

import lombok.Data;

/**
 * @author Andrew Shvayka
 */
@Data
public abstract class TimeoutMsg<T> implements TbActorMsg {
    private final T id;
    private final long timeout;
}
