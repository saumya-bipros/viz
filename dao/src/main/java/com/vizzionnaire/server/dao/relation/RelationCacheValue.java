package com.vizzionnaire.server.dao.relation;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.List;

import com.vizzionnaire.server.common.data.relation.EntityRelation;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
@Builder
public class RelationCacheValue implements Serializable {

    private static final long serialVersionUID = 3911151843961657570L;

    private final EntityRelation relation;
    private final List<EntityRelation> relations;

}
