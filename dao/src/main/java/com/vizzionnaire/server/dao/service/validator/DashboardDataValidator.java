package com.vizzionnaire.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.Dashboard;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import com.vizzionnaire.server.dao.dashboard.DashboardDao;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantService;

@Component
public class DashboardDataValidator extends DataValidator<Dashboard> {

    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Override
    protected void validateCreate(TenantId tenantId, Dashboard data) {
        DefaultTenantProfileConfiguration profileConfiguration =
                (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
        long maxDashboards = profileConfiguration.getMaxDashboards();
        validateNumberOfEntitiesPerTenant(tenantId, dashboardDao, maxDashboards, EntityType.DASHBOARD);
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, Dashboard dashboard) {
        if (StringUtils.isEmpty(dashboard.getTitle())) {
            throw new DataValidationException("Dashboard title should be specified!");
        }
        if (dashboard.getTenantId() == null) {
            throw new DataValidationException("Dashboard should be assigned to tenant!");
        } else {
            if (!tenantService.tenantExists(dashboard.getTenantId())) {
                throw new DataValidationException("Dashboard is referencing to non-existent tenant!");
            }
        }
    }
}
