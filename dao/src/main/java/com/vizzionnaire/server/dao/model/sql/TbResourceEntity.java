package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.vizzionnaire.server.common.data.ResourceType;
import com.vizzionnaire.server.common.data.TbResource;
import com.vizzionnaire.server.common.data.id.TbResourceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.model.BaseSqlEntity;
import com.vizzionnaire.server.dao.model.SearchTextEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import static com.vizzionnaire.server.dao.model.ModelConstants.RESOURCE_DATA_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.RESOURCE_FILE_NAME_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.RESOURCE_KEY_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.RESOURCE_TABLE_NAME;
import static com.vizzionnaire.server.dao.model.ModelConstants.RESOURCE_TENANT_ID_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.RESOURCE_TITLE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.RESOURCE_TYPE_COLUMN;
import static com.vizzionnaire.server.dao.model.ModelConstants.SEARCH_TEXT_PROPERTY;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = RESOURCE_TABLE_NAME)
public class TbResourceEntity extends BaseSqlEntity<TbResource> implements SearchTextEntity<TbResource> {

    @Column(name = RESOURCE_TENANT_ID_COLUMN, columnDefinition = "uuid")
    private UUID tenantId;

    @Column(name = RESOURCE_TITLE_COLUMN)
    private String title;

    @Column(name = RESOURCE_TYPE_COLUMN)
    private String resourceType;

    @Column(name = RESOURCE_KEY_COLUMN)
    private String resourceKey;

    @Column(name = SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Column(name = RESOURCE_FILE_NAME_COLUMN)
    private String fileName;

    @Column(name = RESOURCE_DATA_COLUMN)
    private String data;

    public TbResourceEntity() {
    }

    public TbResourceEntity(TbResource resource) {
        if (resource.getId() != null) {
            this.id = resource.getId().getId();
        }
        this.createdTime = resource.getCreatedTime();
        if (resource.getTenantId() != null) {
            this.tenantId = resource.getTenantId().getId();
        }
        this.title = resource.getTitle();
        this.resourceType = resource.getResourceType().name();
        this.resourceKey = resource.getResourceKey();
        this.searchText = resource.getSearchText();
        this.fileName = resource.getFileName();
        this.data = resource.getData();
    }

    @Override
    public TbResource toData() {
        TbResource resource = new TbResource(new TbResourceId(id));
        resource.setCreatedTime(createdTime);
        resource.setTenantId(TenantId.fromUUID(tenantId));
        resource.setTitle(title);
        resource.setResourceType(ResourceType.valueOf(resourceType));
        resource.setResourceKey(resourceKey);
        resource.setSearchText(searchText);
        resource.setFileName(fileName);
        resource.setData(data);
        return resource;
    }

    @Override
    public String getSearchTextSource() {
        return this.searchText;
    }
}
