package com.vizzionnaire.server.common.data.device.profile.lwm2m.bootstrap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vizzionnaire.server.common.data.device.credentials.lwm2m.LwM2MSecurityMode;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "securityMode")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NoSecLwM2MBootstrapServerCredential.class, name = "NO_SEC"),
        @JsonSubTypes.Type(value = PSKLwM2MBootstrapServerCredential.class, name = "PSK"),
        @JsonSubTypes.Type(value = RPKLwM2MBootstrapServerCredential.class, name = "RPK"),
        @JsonSubTypes.Type(value = X509LwM2MBootstrapServerCredential.class, name = "X509")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public interface LwM2MBootstrapServerCredential {
    @JsonIgnore
    LwM2MSecurityMode getSecurityMode();
}
