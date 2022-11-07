package com.vizzionnaire.server.dao.sqlts;

import com.vizzionnaire.server.dao.model.sql.AbstractTsKvEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EntityContainer<T extends AbstractTsKvEntity> {

        private T entity;
        private String partitionDate;

}
