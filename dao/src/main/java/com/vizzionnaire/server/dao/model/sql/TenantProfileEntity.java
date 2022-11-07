package com.vizzionnaire.server.dao.model.sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.TenantProfileId;
import com.vizzionnaire.server.common.data.tenant.profile.TenantProfileData;
import com.vizzionnaire.server.dao.model.BaseSqlEntity;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.model.SearchTextEntity;
import com.vizzionnaire.server.dao.util.mapping.JsonBinaryType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Table(name = ModelConstants.TENANT_PROFILE_COLUMN_FAMILY_NAME)
public final class TenantProfileEntity extends BaseSqlEntity<TenantProfile> implements SearchTextEntity<TenantProfile> {

    @Column(name = ModelConstants.TENANT_PROFILE_NAME_PROPERTY)
    private String name;

    @Column(name = ModelConstants.TENANT_PROFILE_DESCRIPTION_PROPERTY)
    private String description;

    @Column(name = ModelConstants.SEARCH_TEXT_PROPERTY)
    private String searchText;

    @Column(name = ModelConstants.TENANT_PROFILE_IS_DEFAULT_PROPERTY)
    private boolean isDefault;

    @Column(name = ModelConstants.TENANT_PROFILE_ISOLATED_TB_RULE_ENGINE)
    private boolean isolatedTbRuleEngine;

    @Type(type = "jsonb")
    @Column(name = ModelConstants.TENANT_PROFILE_PROFILE_DATA_PROPERTY, columnDefinition = "jsonb")
    private JsonNode profileData;

    public TenantProfileEntity() {
        super();
    }

    public TenantProfileEntity(TenantProfile tenantProfile) {
        if (tenantProfile.getId() != null) {
            this.setUuid(tenantProfile.getId().getId());
        }
        this.setCreatedTime(tenantProfile.getCreatedTime());
        this.name = tenantProfile.getName();
        this.description = tenantProfile.getDescription();
        this.isDefault = tenantProfile.isDefault();
        this.isolatedTbRuleEngine = tenantProfile.isIsolatedTbRuleEngine();
        this.profileData = JacksonUtil.convertValue(tenantProfile.getProfileData(), ObjectNode.class);
    }

    @Override
    public String getSearchTextSource() {
        return name;
    }

    @Override
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchText() {
        return searchText;
    }

    @Override
    public TenantProfile toData() {
        TenantProfile tenantProfile = new TenantProfile(new TenantProfileId(this.getUuid()));
        tenantProfile.setCreatedTime(createdTime);
        tenantProfile.setName(name);
        tenantProfile.setDescription(description);
        tenantProfile.setDefault(isDefault);
        tenantProfile.setIsolatedTbRuleEngine(isolatedTbRuleEngine);
        tenantProfile.setProfileData(JacksonUtil.convertValue(profileData, TenantProfileData.class));
        return tenantProfile;
    }

}
