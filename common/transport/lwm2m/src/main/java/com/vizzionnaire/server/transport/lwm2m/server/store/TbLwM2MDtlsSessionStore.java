package com.vizzionnaire.server.transport.lwm2m.server.store;


import com.vizzionnaire.server.transport.lwm2m.secure.TbX509DtlsSessionInfo;

public interface TbLwM2MDtlsSessionStore {

    void put(String endpoint, TbX509DtlsSessionInfo msg);

    TbX509DtlsSessionInfo get(String endpoint);

    void remove(String endpoint);

}
