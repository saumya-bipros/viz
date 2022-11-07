package com.vizzionnaire.server.common.data.tenant.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vizzionnaire.server.common.data.ApiUsageRecordKey;
import com.vizzionnaire.server.common.data.TenantProfileType;

import io.swagger.annotations.ApiModel;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultTenantProfileConfiguration.class, name = "DEFAULT")})
public interface TenantProfileConfiguration {

    @JsonIgnore
    TenantProfileType getType();

    @JsonIgnore
    long getProfileThreshold(ApiUsageRecordKey key);

    @JsonIgnore
    long getWarnThreshold(ApiUsageRecordKey key);

    @JsonIgnore
    int getMaxRuleNodeExecsPerMessage();

}
