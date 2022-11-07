package com.vizzionnaire.rule.engine.filter;

import lombok.Data;

import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;

/**
 * Created by ashvayka on 19.01.18.
 */
@Data
public class TbCheckRelationNodeConfiguration implements NodeConfiguration<TbCheckRelationNodeConfiguration> {

    private String direction;
    private String entityId;
    private String entityType;
    private String relationType;
    private boolean checkForSingleEntity;

    @Override
    public TbCheckRelationNodeConfiguration defaultConfiguration() {
        TbCheckRelationNodeConfiguration configuration = new TbCheckRelationNodeConfiguration();
        configuration.setDirection(EntitySearchDirection.FROM.name());
        configuration.setRelationType("Contains");
        configuration.setCheckForSingleEntity(true);
        return configuration;
    }
}
