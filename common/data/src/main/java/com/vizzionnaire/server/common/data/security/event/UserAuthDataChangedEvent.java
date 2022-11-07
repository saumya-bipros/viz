package com.vizzionnaire.server.common.data.security.event;

import com.vizzionnaire.server.common.data.id.UserId;

import lombok.Data;

@Data
public class UserAuthDataChangedEvent {
    private final UserId userId;
    private final long ts;

    public UserAuthDataChangedEvent(UserId userId) {
        this.userId = userId;
        this.ts = System.currentTimeMillis();
    }

}
