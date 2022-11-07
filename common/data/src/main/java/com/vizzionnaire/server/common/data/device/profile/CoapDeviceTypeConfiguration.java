package com.vizzionnaire.server.common.data.device.profile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vizzionnaire.server.common.data.CoapDeviceType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "coapDeviceType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultCoapDeviceTypeConfiguration.class, name = "DEFAULT"),
        @JsonSubTypes.Type(value = EfentoCoapDeviceTypeConfiguration.class, name = "EFENTO")})
public interface CoapDeviceTypeConfiguration {

    @JsonIgnore
    CoapDeviceType getCoapDeviceType();

}
