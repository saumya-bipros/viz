package com.vizzionnaire.server.actors.service;

import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActorCreator;

public abstract class ContextBasedCreator implements TbActorCreator {

    protected final transient ActorSystemContext context;

    public ContextBasedCreator(ActorSystemContext context) {
        super();
        this.context = context;
    }
}
