package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.TypeDef;

import com.vizzionnaire.server.common.data.EntityView;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.util.mapping.JsonStringType;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "json", typeClass = JsonStringType.class)
@Table(name = ModelConstants.ENTITY_VIEW_TABLE_FAMILY_NAME)
public class EntityViewEntity extends AbstractEntityViewEntity<EntityView> {

    public EntityViewEntity() {
        super();
    }

    public EntityViewEntity(EntityView entityView) {
        super(entityView);
    }

    @Override
    public EntityView toData() {
        return super.toEntityView();
    }
}
