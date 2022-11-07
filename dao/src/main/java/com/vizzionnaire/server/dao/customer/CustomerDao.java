package com.vizzionnaire.server.dao.customer;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.Dao;
import com.vizzionnaire.server.dao.ExportableEntityDao;
import com.vizzionnaire.server.dao.TenantEntityDao;

import java.util.Optional;
import java.util.UUID;

/**
 * The Interface CustomerDao.
 */
public interface CustomerDao extends Dao<Customer>, TenantEntityDao, ExportableEntityDao<CustomerId, Customer> {

    /**
     * Save or update customer object
     *
     * @param customer the customer object
     * @return saved customer object
     */
    Customer save(TenantId tenantId, Customer customer);

    /**
     * Find customers by tenant id and page link.
     *
     * @param tenantId the tenant id
     * @param pageLink the page link
     * @return the list of customer objects
     */
    PageData<Customer> findCustomersByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find customers by tenantId and customer title.
     *
     * @param tenantId the tenantId
     * @param title the customer title
     * @return the optional customer object
     */
    Optional<Customer> findCustomersByTenantIdAndTitle(UUID tenantId, String title);

}
