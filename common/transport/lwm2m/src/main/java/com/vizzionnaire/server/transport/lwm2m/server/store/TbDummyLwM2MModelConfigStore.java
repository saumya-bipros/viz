package com.vizzionnaire.server.transport.lwm2m.server.store;

import java.util.Collections;
import java.util.List;

import com.vizzionnaire.server.transport.lwm2m.server.model.LwM2MModelConfig;

public class TbDummyLwM2MModelConfigStore implements TbLwM2MModelConfigStore {
    @Override
    public List<LwM2MModelConfig> getAll() {
        return Collections.emptyList();
    }

    @Override
    public void put(LwM2MModelConfig modelConfig) {

    }

    @Override
    public void remove(String endpoint) {

    }
}
