package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import com.vizzionnaire.server.transport.lwm2m.server.LwM2MOperationType;

import lombok.Builder;
import lombok.Getter;

public class TbLwM2MCancelAllRequest implements TbLwM2MDownlinkRequest<Integer> {

    @Getter
    private final long timeout;

    @Builder
    private TbLwM2MCancelAllRequest(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public LwM2MOperationType getType() {
        return LwM2MOperationType.OBSERVE_CANCEL_ALL;
    }

}
