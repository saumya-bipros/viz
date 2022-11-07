package com.vizzionnaire.server.dao.sqlts.insert;

import java.util.List;

import com.vizzionnaire.server.dao.model.sql.AbstractTsKvEntity;

public interface InsertTsRepository<T extends AbstractTsKvEntity> {

    void saveOrUpdate(List<T> entities);

}
