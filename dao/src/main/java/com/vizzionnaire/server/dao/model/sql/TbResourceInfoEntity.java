package com.vizzionnaire.server.dao.model.sql;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.vizzionnaire.server.common.data.ResourceType;
import com.vizzionnaire.server.common.data.TbResourceInfo;
import com.vizzionnaire.server.common.data.id.TbResourceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.model.BaseSqlEntity;
import com.vizzionnaire.server.dao.model.SearchTextEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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
public class TbResourceInfoEntity extends BaseSqlEntity<TbResourceInfo> implements SearchTextEntity<TbResourceInfo> {

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

    public TbResourceInfoEntity() {
    }

    public TbResourceInfoEntity(TbResourceInfo resource) {
        if (resource.getId() != null) {
            this.id = resource.getId().getId();
        }
        this.createdTime = resource.getCreatedTime();
        this.tenantId = resource.getTenantId().getId();
        this.title = resource.getTitle();
        this.resourceType = resource.getResourceType().name();
        this.resourceKey = resource.getResourceKey();
        this.searchText = resource.getSearchText();
    }

    @Override
    public TbResourceInfo toData() {
        TbResourceInfo resource = new TbResourceInfo(new TbResourceId(id));
        resource.setCreatedTime(createdTime);
        resource.setTenantId(TenantId.fromUUID(tenantId));
        resource.setTitle(title);
        resource.setResourceType(ResourceType.valueOf(resourceType));
        resource.setResourceKey(resourceKey);
        resource.setSearchText(searchText);
        return resource;
    }

    @Override
    public String getSearchTextSource() {
        return title;
    }
}
