package com.vizzionnaire.server.dao.model.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.vizzionnaire.server.common.data.EntityType;

import static com.vizzionnaire.server.dao.model.ModelConstants.ATTRIBUTE_KEY_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.ATTRIBUTE_TYPE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.ENTITY_ID_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.ENTITY_TYPE_COLUMN;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class AttributeKvCompositeKey implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(name = ENTITY_TYPE_COLUMN)
    private EntityType entityType;
    @Column(name = ENTITY_ID_COLUMN, columnDefinition = "uuid")
    private UUID entityId;
    @Column(name = ATTRIBUTE_TYPE_COLUMN)
    private String attributeType;
    @Column(name = ATTRIBUTE_KEY_COLUMN)
    private String attributeKey;
}
