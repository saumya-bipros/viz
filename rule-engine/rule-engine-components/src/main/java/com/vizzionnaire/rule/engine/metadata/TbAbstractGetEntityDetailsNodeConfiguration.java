package com.vizzionnaire.rule.engine.metadata;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.rule.engine.util.EntityDetails;

@Data
public abstract class TbAbstractGetEntityDetailsNodeConfiguration {


    private List<EntityDetails> detailsList;

    private boolean addToMetadata;

}
