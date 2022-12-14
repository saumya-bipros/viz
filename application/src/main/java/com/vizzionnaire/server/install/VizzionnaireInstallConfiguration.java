package com.vizzionnaire.server.install;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.vizzionnaire.server.dao.audit.AuditLogLevelFilter;
import com.vizzionnaire.server.dao.audit.AuditLogLevelProperties;

import java.util.HashMap;

@Configuration
@Profile("install")
public class VizzionnaireInstallConfiguration {

    @Bean
    public AuditLogLevelFilter emptyAuditLogLevelFilter() {
        var props = new AuditLogLevelProperties();
        props.setMask(new HashMap<>());
        return new AuditLogLevelFilter(props);
    }
}
