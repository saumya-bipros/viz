package com.vizzionnaire.server.dao.sql.relation;

import java.util.List;

import com.vizzionnaire.server.dao.model.sql.RelationEntity;

public interface RelationInsertRepository {

    RelationEntity saveOrUpdate(RelationEntity entity);

    void saveOrUpdate(List<RelationEntity> entities);

}