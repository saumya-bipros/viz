package com.vizzionnaire.rule.engine.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vizzionnaire.rule.engine.api.NodeConfiguration;
import com.vizzionnaire.rule.engine.credentials.AnonymousCredentials;
import com.vizzionnaire.rule.engine.credentials.ClientCredentials;

import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TbRestApiCallNodeConfiguration implements NodeConfiguration<TbRestApiCallNodeConfiguration> {

    private String restEndpointUrlPattern;
    private String requestMethod;
    private Map<String, String> headers;
    private boolean useSimpleClientHttpFactory;
    private int readTimeoutMs;
    private int maxParallelRequestsCount;
    private boolean useRedisQueueForMsgPersistence;
    private boolean trimQueue;
    private int maxQueueSize;
    private boolean enableProxy;
    private boolean useSystemProxyProperties;
    private String proxyHost;
    private int proxyPort;
    private String proxyUser;
    private String proxyPassword;
    private String proxyScheme;
    private ClientCredentials credentials;
    private boolean ignoreRequestBody;

    @Override
    public TbRestApiCallNodeConfiguration defaultConfiguration() {
        TbRestApiCallNodeConfiguration configuration = new TbRestApiCallNodeConfiguration();
        configuration.setRestEndpointUrlPattern("http://localhost/api");
        configuration.setRequestMethod("POST");
        configuration.setHeaders(Collections.singletonMap(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        configuration.setUseSimpleClientHttpFactory(false);
        configuration.setReadTimeoutMs(0);
        configuration.setMaxParallelRequestsCount(0);
        configuration.setUseRedisQueueForMsgPersistence(false);
        configuration.setTrimQueue(false);
        configuration.setEnableProxy(false);
        configuration.setCredentials(new AnonymousCredentials());
        configuration.setIgnoreRequestBody(false);
        return configuration;
    }

    public ClientCredentials getCredentials() {
        if (this.credentials == null) {
            return new AnonymousCredentials();
        } else {
            return this.credentials;
        }
    }
}
