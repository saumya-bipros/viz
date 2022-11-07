package com.vizzionnaire.rule.engine.metadata;

import lombok.Data;

import java.util.Collections;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;

@Data
public class TbGetTenantDetailsNodeConfiguration extends TbAbstractGetEntityDetailsNodeConfiguration implements NodeConfiguration<TbGetTenantDetailsNodeConfiguration> {


    @Override
    public TbGetTenantDetailsNodeConfiguration defaultConfiguration() {
        TbGetTenantDetailsNodeConfiguration configuration = new TbGetTenantDetailsNodeConfiguration();
        configuration.setDetailsList(Collections.emptyList());
        return configuration;
    }
}
