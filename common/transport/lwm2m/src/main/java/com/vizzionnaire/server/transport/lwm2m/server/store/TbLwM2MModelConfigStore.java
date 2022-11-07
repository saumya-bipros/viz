package com.vizzionnaire.server.transport.lwm2m.server.store;

import java.util.List;

import com.vizzionnaire.server.transport.lwm2m.server.model.LwM2MModelConfig;

public interface TbLwM2MModelConfigStore {
    List<LwM2MModelConfig> getAll();

    void put(LwM2MModelConfig modelConfig);

    void remove(String endpoint);
}
