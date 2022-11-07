package com.vizzionnaire.rule.engine.metadata;

import lombok.Data;

import java.util.Collections;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
public class TbGetCustomerDetailsNodeConfiguration extends TbAbstractGetEntityDetailsNodeConfiguration implements NodeConfiguration<TbGetCustomerDetailsNodeConfiguration> {


    @Override
    public TbGetCustomerDetailsNodeConfiguration defaultConfiguration() {
        TbGetCustomerDetailsNodeConfiguration configuration = new TbGetCustomerDetailsNodeConfiguration();
        configuration.setDetailsList(Collections.emptyList());
        return configuration;
    }
}
