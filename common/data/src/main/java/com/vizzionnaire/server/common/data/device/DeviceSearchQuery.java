package com.vizzionnaire.server.common.data.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.common.data.relation.EntityRelationsQuery;
import com.vizzionnaire.server.common.data.relation.RelationEntityTypeFilter;
import com.vizzionnaire.server.common.data.relation.RelationsSearchParameters;

@ApiModel
@Data
public class DeviceSearchQuery {

    @ApiModelProperty(position = 3, value = "Main search parameters.")
    private RelationsSearchParameters parameters;
    @ApiModelProperty(position = 1, value = "Type of the relation between root entity and device (e.g. 'Contains' or 'Manages').")
    private String relationType;
    @ApiModelProperty(position = 2, value = "Array of device types to filter the related entities (e.g. 'Temperature Sensor', 'Smoke Sensor').")
    private List<String> deviceTypes;

    public EntityRelationsQuery toEntitySearchQuery() {
        EntityRelationsQuery query = new EntityRelationsQuery();
        query.setParameters(parameters);
        query.setFilters(
                Collections.singletonList(new RelationEntityTypeFilter(relationType == null ? EntityRelation.CONTAINS_TYPE : relationType,
                        Collections.singletonList(EntityType.DEVICE))));
        return query;
    }
}
