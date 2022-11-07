package com.vizzionnaire.rule.engine.metadata;

import lombok.Data;

import java.util.Collections;
import java.util.List;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

/**
 * Created by ashvayka on 19.01.18.
 */
@Data
public class TbGetAttributesNodeConfiguration implements NodeConfiguration<TbGetAttributesNodeConfiguration> {

    private List<String> clientAttributeNames;
    private List<String> sharedAttributeNames;
    private List<String> serverAttributeNames;

    private List<String> latestTsKeyNames;

    private boolean tellFailureIfAbsent;
    private boolean getLatestValueWithTs;

    @Override
    public TbGetAttributesNodeConfiguration defaultConfiguration() {
        TbGetAttributesNodeConfiguration configuration = new TbGetAttributesNodeConfiguration();
        configuration.setClientAttributeNames(Collections.emptyList());
        configuration.setSharedAttributeNames(Collections.emptyList());
        configuration.setServerAttributeNames(Collections.emptyList());
        configuration.setLatestTsKeyNames(Collections.emptyList());
        configuration.setTellFailureIfAbsent(true);
        configuration.setGetLatestValueWithTs(false);
        return configuration;
    }
}
