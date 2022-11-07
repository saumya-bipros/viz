package com.vizzionnaire.server.dao.sqlts.insert.latest;

import java.util.List;

import com.vizzionnaire.server.dao.model.sqlts.latest.TsKvLatestEntity;

public interface InsertLatestTsRepository {

    void saveOrUpdate(List<TsKvLatestEntity> entities);

}
