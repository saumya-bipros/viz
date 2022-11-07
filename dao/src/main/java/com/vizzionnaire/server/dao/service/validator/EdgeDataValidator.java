package com.vizzionnaire.server.dao.service.validator;

import lombok.AllArgsConstructor;

import static com.vizzionnaire.server.dao.model.ModelConstants.NULL_UUID;

import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.customer.CustomerDao;
import com.vizzionnaire.server.dao.edge.EdgeDao;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TenantService;

@Component
@AllArgsConstructor
public class EdgeDataValidator extends DataValidator<Edge> {

    private final EdgeDao edgeDao;
    private final TenantService tenantService;
    private final CustomerDao customerDao;

    @Override
    protected void validateCreate(TenantId tenantId, Edge edge) {
    }

    @Override
    protected Edge validateUpdate(TenantId tenantId, Edge edge) {
        return edgeDao.findById(edge.getTenantId(), edge.getId().getId());
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Edge edge) {
        if (StringUtils.isEmpty(edge.getType())) {
            throw new DataValidationException("Edge type should be specified!");
        }
        if (StringUtils.isEmpty(edge.getName())) {
            throw new DataValidationException("Edge name should be specified!");
        }
        if (StringUtils.isEmpty(edge.getSecret())) {
            throw new DataValidationException("Edge secret should be specified!");
        }
        if (StringUtils.isEmpty(edge.getRoutingKey())) {
            throw new DataValidationException("Edge routing key should be specified!");
        }
        if (edge.getTenantId() == null) {
            throw new DataValidationException("Edge should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(edge.getTenantId())) {
                throw new DataValidationException("Edge is referencing to non-existent tenant!");
            }
        }
        if (edge.getCustomerId() == null) {
            edge.setCustomerId(new CustomerId(NULL_UUID));
        } else if (!edge.getCustomerId().getId().equals(NULL_UUID)) {
            Customer customer = customerDao.findById(edge.getTenantId(), edge.getCustomerId().getId());
            if (customer == null) {
                throw new DataValidationException("Can't assign edge to non-existent customer!");
            }
            if (!customer.getTenantId().getId().equals(edge.getTenantId().getId())) {
                throw new DataValidationException("Can't assign edge to customer from different tenant!");
            }
        }
    }
}
