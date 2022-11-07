package com.vizzionnaire.server.dao.sql.component;

import com.vizzionnaire.server.dao.model.sql.ComponentDescriptorEntity;

public interface ComponentDescriptorInsertRepository {

    ComponentDescriptorEntity saveOrUpdate(ComponentDescriptorEntity entity);

}
