package com.vizzionnaire.server.transport.lwm2m.server.store;

import java.util.Collections;
import java.util.Set;

import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;

public class TbDummyLwM2MClientStore implements TbLwM2MClientStore {
    @Override
    public LwM2mClient get(String endpoint) {
        return null;
    }

    @Override
    public Set<LwM2mClient> getAll() {
        return Collections.emptySet();
    }

    @Override
    public void put(LwM2mClient client) {

    }

    @Override
    public void remove(String endpoint) {

    }
}
