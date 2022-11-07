package com.vizzionnaire.server.dao.service.validator;

import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.audit.AuditLog;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.service.DataValidator;

@Component
public class AuditLogDataValidator extends DataValidator<AuditLog> {

    @Override
    protected void validateDataImpl(TenantId tenantId, AuditLog auditLog) {
        if (auditLog.getEntityId() == null) {
            throw new DataValidationException("Entity Id should be specified!");
        }
        if (auditLog.getTenantId() == null) {
            throw new DataValidationException("Tenant Id should be specified!");
        }
        if (auditLog.getUserId() == null) {
            throw new DataValidationException("User Id should be specified!");
        }
    }
}
