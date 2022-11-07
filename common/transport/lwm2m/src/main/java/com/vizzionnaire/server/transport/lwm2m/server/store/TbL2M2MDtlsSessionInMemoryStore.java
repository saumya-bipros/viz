package com.vizzionnaire.server.transport.lwm2m.server.store;

import java.util.concurrent.ConcurrentHashMap;

import com.vizzionnaire.server.transport.lwm2m.secure.TbX509DtlsSessionInfo;

public class TbL2M2MDtlsSessionInMemoryStore implements TbLwM2MDtlsSessionStore {

    private final ConcurrentHashMap<String, TbX509DtlsSessionInfo> store = new ConcurrentHashMap<>();

    @Override
    public void put(String endpoint, TbX509DtlsSessionInfo msg) {
        store.put(endpoint, msg);
    }

    @Override
    public TbX509DtlsSessionInfo get(String endpoint) {
        return store.get(endpoint);
    }

    @Override
    public void remove(String endpoint) {
        store.remove(endpoint);
    }
}
