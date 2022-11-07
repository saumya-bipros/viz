package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import com.vizzionnaire.server.transport.lwm2m.utils.LwM2MTransportUtil;

public interface HasVersionedId {

    String getVersionedId();

    default String getObjectId(){
        return LwM2MTransportUtil.fromVersionedIdToObjectId(getVersionedId());
    }

}
