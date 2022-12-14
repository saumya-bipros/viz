package com.vizzionnaire.rule.engine.action;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

import lombok.Data;

@Data
public class TbAssignToCustomerNodeConfiguration extends TbAbstractCustomerActionNodeConfiguration implements NodeConfiguration<TbAssignToCustomerNodeConfiguration> {

    private boolean createCustomerIfNotExists;

    @Override
    public TbAssignToCustomerNodeConfiguration defaultConfiguration() {
        TbAssignToCustomerNodeConfiguration configuration = new TbAssignToCustomerNodeConfiguration();
        configuration.setCustomerNamePattern("");
        configuration.setCreateCustomerIfNotExists(false);
        configuration.setCustomerCacheExpiration(300);
        return configuration;
    }
}
