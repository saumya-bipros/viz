package com.vizzionnaire.rule.engine.action;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;

@Data
public class TbDeleteRelationNodeConfiguration extends TbAbstractRelationActionNodeConfiguration implements NodeConfiguration<TbDeleteRelationNodeConfiguration> {

    private boolean deleteForSingleEntity;

    @Override
    public TbDeleteRelationNodeConfiguration defaultConfiguration() {
        TbDeleteRelationNodeConfiguration configuration = new TbDeleteRelationNodeConfiguration();
        configuration.setDeleteForSingleEntity(true);
        configuration.setDirection(EntitySearchDirection.FROM.name());
        configuration.setRelationType("Contains");
        configuration.setEntityNamePattern("");
        configuration.setEntityCacheExpiration(300);
        return configuration;
    }
}
