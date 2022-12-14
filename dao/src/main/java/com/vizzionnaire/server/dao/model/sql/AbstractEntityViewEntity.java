package com.vizzionnaire.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.EntityView;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.objects.TelemetryEntityView;
import com.vizzionnaire.server.dao.model.BaseSqlEntity;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.model.SearchTextEntity;
import com.vizzionnaire.server.dao.util.mapping.JsonStringType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import static com.vizzionnaire.server.dao.model.ModelConstants.ENTITY_TYPE_PROPERTY;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Victor Basanets on 8/30/2017.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TypeDef(name = "json", typeClass = JsonStringType.class)
@MappedSuperclass
@Slf4j
public abstract class AbstractEntityViewEntity<T extends EntityView> extends BaseSqlEntity<T> implements SearchTextEntity<T> {

    @Column(name = ModelConstants.ENTITY_VIEW_ENTITY_ID_PROPERTY)
    private UUID entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = ENTITY_TYPE_PROPERTY)
    private EntityType entityType;

    @Column(name = ModelConstants.ENTITY_VIEW_TENANT_ID_PROPERTY)
    private UUID tenantId;

    @Column(name = ModelConstants.ENTITY_VIEW_CUSTOMER_ID_PROPERTY)
    private UUID customerId;

    @Column(name = ModelConstants.DEVICE_TYPE_PROPERTY)
    private String type;

    @Column(name = ModelConstants.ENTITY_VIEW_NAME_PROPERTY)
    private String name;

    @Column(name = ModelConstants.ENTITY_VIEW_KEYS_PROPERTY)
    private String keys;

    @Column(name = ModelConstants.ENTITY_VIEW_START_TS_PROPERTY)
    private long startTs;

    @Column(name = ModelConstants.ENTITY_VIEW_END_TS_PROPERTY)
    private long endTs;

    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Type(type = "json")
    @Column(name = ModelConstants.ENTITY_VIEW_ADDITIONAL_INFO_PROPERTY)
    private JsonNode additionalInfo;

    @Column(name = ModelConstants.EXTERNAL_ID_PROPERTY)
    private UUID externalId;

    private static final ObjectMapper mapper = new ObjectMapper();

    public AbstractEntityViewEntity() {
        super();
    }

    public AbstractEntityViewEntity(EntityView entityView) {
        if (entityView.getId() != null) {
            this.setUuid(entityView.getId().getId());
        }
        this.setCreatedTime(entityView.getCreatedTime());
        if (entityView.getEntityId() != null) {
            this.entityId = entityView.getEntityId().getId();
            this.entityType = entityView.getEntityId().getEntityType();
        }
        if (entityView.getTenantId() != null) {
            this.tenantId = entityView.getTenantId().getId();
        }
        if (entityView.getCustomerId() != null) {
            this.customerId = entityView.getCustomerId().getId();
        }
        this.type = entityView.getType();
        this.name = entityView.getName();
        try {
            this.keys = mapper.writeValueAsString(entityView.getKeys());
        } catch (IOException e) {
            log.error("Unable to serialize entity view keys!", e);
        }
        this.startTs = entityView.getStartTimeMs();
        this.endTs = entityView.getEndTimeMs();
        this.searchText = entityView.getSearchText();
        this.additionalInfo = entityView.getAdditionalInfo();
        if (entityView.getExternalId() != null) {
            this.externalId = entityView.getExternalId().getId();
        }
    }

    public AbstractEntityViewEntity(EntityViewEntity entityViewEntity) {
        this.setId(entityViewEntity.getId());
        this.setCreatedTime(entityViewEntity.getCreatedTime());
        this.entityId = entityViewEntity.getEntityId();
        this.entityType = entityViewEntity.getEntityType();
        this.tenantId = entityViewEntity.getTenantId();
        this.customerId = entityViewEntity.getCustomerId();
        this.type = entityViewEntity.getType();
        this.name = entityViewEntity.getName();
        this.keys = entityViewEntity.getKeys();
        this.startTs = entityViewEntity.getStartTs();
        this.endTs = entityViewEntity.getEndTs();
        this.searchText = entityViewEntity.getSearchText();
        this.additionalInfo = entityViewEntity.getAdditionalInfo();
        this.externalId = entityViewEntity.getExternalId();
    }

    @Override
    public String getSearchTextSource() {
        return name;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    protected EntityView toEntityView() {
        EntityView entityView = new EntityView(new EntityViewId(getUuid()));
        entityView.setCreatedTime(createdTime);

        if (entityId != null) {
            entityView.setEntityId(EntityIdFactory.getByTypeAndUuid(entityType.name(), entityId));
        }
        if (tenantId != null) {
            entityView.setTenantId(TenantId.fromUUID(tenantId));
        }
        if (customerId != null) {
            entityView.setCustomerId(new CustomerId(customerId));
        }
        entityView.setType(type);
        entityView.setName(name);
        try {
            entityView.setKeys(mapper.readValue(keys, TelemetryEntityView.class));
        } catch (IOException e) {
            log.error("Unable to read entity view keys!", e);
        }
        entityView.setStartTimeMs(startTs);
        entityView.setEndTimeMs(endTs);
        entityView.setAdditionalInfo(additionalInfo);
        if (externalId != null) {
            entityView.setExternalId(new EntityViewId(externalId));
        }
        return entityView;
    }
}
