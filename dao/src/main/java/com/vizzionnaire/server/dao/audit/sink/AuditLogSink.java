package com.vizzionnaire.server.dao.audit.sink;

import com.vizzionnaire.server.common.data.audit.AuditLog;

public interface AuditLogSink {

    void logAction(AuditLog auditLogEntry);
}
