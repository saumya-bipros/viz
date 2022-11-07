package com.vizzionnaire.rule.engine.action;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbUnassignFromCustomerNodeConfiguration extends TbAbstractCustomerActionNodeConfiguration implements NodeConfiguration<TbUnassignFromCustomerNodeConfiguration> {

    @Override
    public TbUnassignFromCustomerNodeConfiguration defaultConfiguration() {
        TbUnassignFromCustomerNodeConfiguration configuration = new TbUnassignFromCustomerNodeConfiguration();
        configuration.setCustomerNamePattern("");
        configuration.setCustomerCacheExpiration(300);
        return configuration;
    }
}
