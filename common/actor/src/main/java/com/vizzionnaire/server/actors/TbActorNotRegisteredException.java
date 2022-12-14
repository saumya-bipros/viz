package com.vizzionnaire.server.actors;

import lombok.Getter;

public class TbActorNotRegisteredException extends RuntimeException {

    @Getter
    private TbActorId target;

    public TbActorNotRegisteredException(TbActorId target, String message) {
        super(message);
        this.target = target;
    }
}
