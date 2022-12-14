package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import com.vizzionnaire.server.transport.lwm2m.server.LwM2MOperationType;

import lombok.Builder;

public class TbLwM2MCancelObserveRequest extends AbstractTbLwM2MTargetedDownlinkRequest<Integer> {

    @Builder
    private TbLwM2MCancelObserveRequest(String versionedId, long timeout) {
        super(versionedId, timeout);
    }

    @Override
    public LwM2MOperationType getType() {
        return LwM2MOperationType.OBSERVE_CANCEL;
    }



}
