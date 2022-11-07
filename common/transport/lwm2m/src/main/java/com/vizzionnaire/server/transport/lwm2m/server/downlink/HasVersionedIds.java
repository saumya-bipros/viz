package com.vizzionnaire.server.transport.lwm2m.server.downlink;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.vizzionnaire.server.transport.lwm2m.utils.LwM2MTransportUtil;

public interface HasVersionedIds {

    String[] getVersionedIds();

    default String[] getObjectIds() {
        Set<String> objectIds = ConcurrentHashMap.newKeySet();
        for (String versionedId : getVersionedIds()) {
            objectIds.add(LwM2MTransportUtil.fromVersionedIdToObjectId(versionedId));
        }
        return objectIds.toArray(String[]::new);
    }

}
