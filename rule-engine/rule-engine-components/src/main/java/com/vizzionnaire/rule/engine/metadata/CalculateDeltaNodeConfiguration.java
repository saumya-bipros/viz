package com.vizzionnaire.rule.engine.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculateDeltaNodeConfiguration implements NodeConfiguration<CalculateDeltaNodeConfiguration> {
    private String inputValueKey;
    private String outputValueKey;
    private boolean useCache;
    private boolean addPeriodBetweenMsgs;
    private String periodValueKey;
    private Integer round;
    private boolean tellFailureIfDeltaIsNegative;

    @Override
    public CalculateDeltaNodeConfiguration defaultConfiguration() {
        CalculateDeltaNodeConfiguration configuration = new CalculateDeltaNodeConfiguration();
        configuration.setInputValueKey("pulseCounter");
        configuration.setOutputValueKey("delta");
        configuration.setUseCache(true);
        configuration.setAddPeriodBetweenMsgs(false);
        configuration.setPeriodValueKey("periodInMs");
        configuration.setTellFailureIfDeltaIsNegative(true);
        return configuration;
    }

}
