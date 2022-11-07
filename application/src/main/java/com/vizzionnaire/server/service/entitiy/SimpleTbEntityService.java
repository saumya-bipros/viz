package com.vizzionnaire.server.service.entitiy;

import com.vizzionnaire.server.common.data.User;

public interface SimpleTbEntityService<T> {

    default T save(T entity) throws Exception {
        return save(entity, null);
    }

    T save(T entity, User user) throws Exception;

    void delete(T entity, User user);

}
