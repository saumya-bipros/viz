package com.vizzionnaire.server.dao.service.validator;

import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.service.DataValidator;

@Component
public class EdgeEventDataValidator extends DataValidator<EdgeEvent> {

    @Override
    protected void validateDataImpl(TenantId tenantId, EdgeEvent edgeEvent) {
        if (edgeEvent.getEdgeId() == null) {
            throw new DataValidationException("Edge id should be specified!");
        }
        if (edgeEvent.getAction() == null) {
            throw new DataValidationException("Edge Event action should be specified!");
        }
    }
}
