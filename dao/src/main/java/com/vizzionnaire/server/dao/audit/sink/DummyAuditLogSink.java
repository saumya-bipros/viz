package com.vizzionnaire.server.dao.audit.sink;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.audit.AuditLog;

@Component
@ConditionalOnProperty(prefix = "audit-log.sink", value = "type", havingValue = "none")
public class DummyAuditLogSink implements AuditLogSink {

    @Override
    public void logAction(AuditLog auditLogEntry) {
    }
}
