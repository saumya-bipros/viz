package com.vizzionnaire.server.actors;

public interface TbActorCreator {

    TbActorId createActorId();

    TbActor createActor();

}
