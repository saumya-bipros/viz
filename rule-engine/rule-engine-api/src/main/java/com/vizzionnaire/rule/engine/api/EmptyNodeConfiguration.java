package com.vizzionnaire.rule.engine.api;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
public class EmptyNodeConfiguration implements NodeConfiguration<EmptyNodeConfiguration> {

    private int version;

    @Override
    public EmptyNodeConfiguration defaultConfiguration() {
        EmptyNodeConfiguration configuration = new EmptyNodeConfiguration();
        return configuration;
    }
}
