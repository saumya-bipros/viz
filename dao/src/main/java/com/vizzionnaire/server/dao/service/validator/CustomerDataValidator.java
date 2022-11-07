package com.vizzionnaire.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import com.vizzionnaire.server.dao.customer.CustomerDao;
import com.vizzionnaire.server.dao.customer.CustomerServiceImpl;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantService;

import java.util.Optional;

@Component
public class CustomerDataValidator extends DataValidator<Customer> {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Override
    protected void validateCreate(TenantId tenantId, Customer customer) {
        DefaultTenantProfileConfiguration profileConfiguration =
                (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
        long maxCustomers = profileConfiguration.getMaxCustomers();

        validateNumberOfEntitiesPerTenant(tenantId, customerDao, maxCustomers, EntityType.CUSTOMER);
        customerDao.findCustomersByTenantIdAndTitle(customer.getTenantId().getId(), customer.getTitle()).ifPresent(
                c -> {
                    throw new DataValidationException("Customer with such title already exists!");
                }
        );
    }

    @Override
    protected Customer validateUpdate(TenantId tenantId, Customer customer) {
        Optional<Customer> customerOpt = customerDao.findCustomersByTenantIdAndTitle(customer.getTenantId().getId(), customer.getTitle());
        customerOpt.ifPresent(
                c -> {
                    if (!c.getId().equals(customer.getId())) {
                        throw new DataValidationException("Customer with such title already exists!");
                    }
                }
        );
        return customerOpt.orElse(null);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Customer customer) {
        if (StringUtils.isEmpty(customer.getTitle())) {
            throw new DataValidationException("Customer title should be specified!");
        }
        if (customer.getTitle().equals(CustomerServiceImpl.PUBLIC_CUSTOMER_TITLE)) {
            throw new DataValidationException("'Public' title for customer is system reserved!");
        }
        if (!StringUtils.isEmpty(customer.getEmail())) {
            validateEmail(customer.getEmail());
        }
        if (customer.getTenantId() == null) {
            throw new DataValidationException("Customer should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(customer.getTenantId())) {
                throw new DataValidationException("Customer is referencing to non-existent tenant!");
            }
        }
    }
}
