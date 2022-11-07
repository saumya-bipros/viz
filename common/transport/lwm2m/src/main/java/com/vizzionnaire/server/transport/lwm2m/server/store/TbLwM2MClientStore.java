package com.vizzionnaire.server.transport.lwm2m.server.store;

import java.util.Set;

import com.vizzionnaire.server.transport.lwm2m.server.client.LwM2mClient;

public interface TbLwM2MClientStore {

    LwM2mClient get(String endpoint);

    Set<LwM2mClient> getAll();

    void put(LwM2mClient client);

    void remove(String endpoint);
}
