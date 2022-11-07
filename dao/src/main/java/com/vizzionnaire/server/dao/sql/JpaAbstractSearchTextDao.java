package com.vizzionnaire.server.dao.sql;

import com.vizzionnaire.server.dao.model.BaseEntity;
import com.vizzionnaire.server.dao.model.SearchTextEntity;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
public abstract class JpaAbstractSearchTextDao <E extends BaseEntity<D>, D> extends JpaAbstractDao<E, D> {

    @Override
    protected void setSearchText(E entity) {
        ((SearchTextEntity) entity).setSearchText(((SearchTextEntity) entity).getSearchTextSource().toLowerCase());
    }
}
