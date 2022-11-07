package com.vizzionnaire.rule.engine.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.vizzionnaire.rule.engine.mqtt.azure.AzureIotHubSasCredentials;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnonymousCredentials.class, name = "anonymous"),
        @JsonSubTypes.Type(value = BasicCredentials.class, name = "basic"),
        @JsonSubTypes.Type(value = AzureIotHubSasCredentials.class, name = "sas"),
        @JsonSubTypes.Type(value = CertPemCredentials.class, name = "cert.PEM")})
public interface ClientCredentials {
    @JsonIgnore
    CredentialsType getType();

    @JsonIgnore
    default SslContext initSslContext() throws SSLException{
        return SslContextBuilder.forClient().build();
    }
}
