package com.vizzionnaire.server.actors;

import com.vizzionnaire.server.common.msg.TbActorMsg;

public interface TbActorRef {

    TbActorId getActorId();

    void tell(TbActorMsg actorMsg);

    void tellWithHighPriority(TbActorMsg actorMsg);

}
