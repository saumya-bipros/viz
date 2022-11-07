package com.vizzionnaire.server.dao.service.validator;

import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.event.Event;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.service.DataValidator;

@Component
public class EventDataValidator extends DataValidator<Event> {

    @Override
    protected void validateDataImpl(TenantId tenantId, Event event) {
        if (event.getTenantId() == null) {
            throw new DataValidationException("Tenant id should be specified!.");
        }
        if (event.getEntityId() == null) {
            throw new DataValidationException("Entity id should be specified!.");
        }
        if (StringUtils.isEmpty(event.getServiceId())) {
            throw new DataValidationException("Service id should be specified!.");
        }
    }
}
