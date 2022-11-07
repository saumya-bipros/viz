package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

import com.vizzionnaire.server.transport.lwm2m.server.LwM2MOperationType;

public class TbLwM2MObserveAllRequest implements TbLwM2MDownlinkRequest<Set<String>> {

    @Getter
    private final long timeout;

    @Builder
    private TbLwM2MObserveAllRequest(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public LwM2MOperationType getType() {
        return LwM2MOperationType.OBSERVE_READ_ALL;
    }



}
