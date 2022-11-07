package com.vizzionnaire.rule.engine.metadata;

import lombok.Data;

import com.vizzionnaire.rule.engine.data.RelationsQuery;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;
import com.vizzionnaire.server.common.data.relation.RelationEntityTypeFilter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
public class TbGetRelatedAttrNodeConfiguration extends TbGetEntityAttrNodeConfiguration {

    private RelationsQuery relationsQuery;

    @Override
    public TbGetRelatedAttrNodeConfiguration defaultConfiguration() {
        TbGetRelatedAttrNodeConfiguration configuration = new TbGetRelatedAttrNodeConfiguration();
        Map<String, String> attrMapping = new HashMap<>();
        attrMapping.putIfAbsent("temperature", "tempo");
        configuration.setAttrMapping(attrMapping);
        configuration.setTelemetry(false);

        RelationsQuery relationsQuery = new RelationsQuery();
        relationsQuery.setDirection(EntitySearchDirection.FROM);
        relationsQuery.setMaxLevel(1);
        RelationEntityTypeFilter relationEntityTypeFilter = new RelationEntityTypeFilter(EntityRelation.CONTAINS_TYPE, Collections.emptyList());
        relationsQuery.setFilters(Collections.singletonList(relationEntityTypeFilter));
        configuration.setRelationsQuery(relationsQuery);

        return configuration;
    }
}
