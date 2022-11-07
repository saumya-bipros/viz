package com.vizzionnaire.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.dao.customer.CustomerDao;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class CustomerImportService extends BaseEntityImportService<CustomerId, Customer, EntityExportData<Customer>> {

    private final CustomerService customerService;
    private final CustomerDao customerDao;

    @Override
    protected void setOwner(TenantId tenantId, Customer customer, IdProvider idProvider) {
        customer.setTenantId(tenantId);
    }

    @Override
    protected Customer prepare(EntitiesImportCtx ctx, Customer customer, Customer old, EntityExportData<Customer> exportData, IdProvider idProvider) {
        if (customer.isPublic()) {
            Customer publicCustomer = customerService.findOrCreatePublicCustomer(ctx.getTenantId());
            publicCustomer.setExternalId(customer.getExternalId());
            return publicCustomer;
        } else {
            return customer;
        }
    }

    @Override
    protected Customer saveOrUpdate(EntitiesImportCtx ctx, Customer customer, EntityExportData<Customer> exportData, IdProvider idProvider) {
        if (!customer.isPublic()) {
            return customerService.saveCustomer(customer);
        } else {
            return customerDao.save(ctx.getTenantId(), customer);
        }
    }

    @Override
    protected Customer deepCopy(Customer customer) {
        return new Customer(customer);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.CUSTOMER;
    }

}
