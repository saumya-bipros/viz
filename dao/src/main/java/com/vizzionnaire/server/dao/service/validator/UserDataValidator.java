package com.vizzionnaire.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.security.Authority;
import com.vizzionnaire.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import com.vizzionnaire.server.dao.customer.CustomerDao;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.user.UserDao;
import com.vizzionnaire.server.dao.user.UserService;

@Component
public class UserDataValidator extends DataValidator<User> {

    @Autowired
    private UserDao userDao;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Autowired
    @Lazy
    private TenantService tenantService;

    @Override
    protected void validateCreate(TenantId tenantId, User user) {
        if (!user.getTenantId().getId().equals(ModelConstants.NULL_UUID)) {
            DefaultTenantProfileConfiguration profileConfiguration =
                    (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
            long maxUsers = profileConfiguration.getMaxUsers();
            validateNumberOfEntitiesPerTenant(tenantId, userDao, maxUsers, EntityType.USER);
        }
    }

    @Override
    protected void validateDataImpl(TenantId requestTenantId, User user) {
        if (StringUtils.isEmpty(user.getEmail())) {
            throw new DataValidationException("User email should be specified!");
        }

        validateEmail(user.getEmail());

        Authority authority = user.getAuthority();
        if (authority == null) {
            throw new DataValidationException("User authority isn't defined!");
        }
        TenantId tenantId = user.getTenantId();
        if (tenantId == null) {
            tenantId = TenantId.fromUUID(ModelConstants.NULL_UUID);
            user.setTenantId(tenantId);
        }
        CustomerId customerId = user.getCustomerId();
        if (customerId == null) {
            customerId = new CustomerId(ModelConstants.NULL_UUID);
            user.setCustomerId(customerId);
        }

        switch (authority) {
            case SYS_ADMIN:
                if (!tenantId.getId().equals(ModelConstants.NULL_UUID)
                        || !customerId.getId().equals(ModelConstants.NULL_UUID)) {
                    throw new DataValidationException("System administrator can't be assigned neither to tenant nor to customer!");
                }
                break;
            case TENANT_ADMIN:
                if (tenantId.getId().equals(ModelConstants.NULL_UUID)) {
                    throw new DataValidationException("Tenant administrator should be assigned to tenant!");
                } else if (!customerId.getId().equals(ModelConstants.NULL_UUID)) {
                    throw new DataValidationException("Tenant administrator can't be assigned to customer!");
                }
                break;
            case CUSTOMER_USER:
                if (tenantId.getId().equals(ModelConstants.NULL_UUID)
                        || customerId.getId().equals(ModelConstants.NULL_UUID)) {
                    throw new DataValidationException("Customer user should be assigned to customer!");
                }
                break;
            default:
                break;
        }

        User existentUserWithEmail = userService.findUserByEmail(tenantId, user.getEmail());
        if (existentUserWithEmail != null && !isSameData(existentUserWithEmail, user)) {
            throw new DataValidationException("User with email '" + user.getEmail() + "' "
                    + " already present in database!");
        }
        if (!tenantId.getId().equals(ModelConstants.NULL_UUID)) {
            if (!tenantService.tenantExists(user.getTenantId())) {
                throw new DataValidationException("User is referencing to non-existent tenant!");
            }
        }
        if (!customerId.getId().equals(ModelConstants.NULL_UUID)) {
            Customer customer = customerDao.findById(tenantId, user.getCustomerId().getId());
            if (customer == null) {
                throw new DataValidationException("User is referencing to non-existent customer!");
            } else if (!customer.getTenantId().getId().equals(tenantId.getId())) {
                throw new DataValidationException("User can't be assigned to customer from different tenant!");
            }
        }
    }
}
