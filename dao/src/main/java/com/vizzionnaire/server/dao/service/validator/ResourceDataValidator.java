package com.vizzionnaire.server.dao.service.validator;

import static com.vizzionnaire.server.common.data.EntityType.TB_RESOURCE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.TbResource;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.resource.TbResourceDao;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantService;

@Component
public class ResourceDataValidator extends DataValidator<TbResource> {

    @Autowired
    private TbResourceDao resourceDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    @Lazy
    private TbTenantProfileCache tenantProfileCache;

    @Override
    protected void validateCreate(TenantId tenantId, TbResource resource) {
        if (tenantId != null && !TenantId.SYS_TENANT_ID.equals(tenantId)) {
            DefaultTenantProfileConfiguration profileConfiguration =
                    (DefaultTenantProfileConfiguration) tenantProfileCache.get(tenantId).getProfileData().getConfiguration();
            long maxSumResourcesDataInBytes = profileConfiguration.getMaxResourcesInBytes();
            validateMaxSumDataSizePerTenant(tenantId, resourceDao, maxSumResourcesDataInBytes, resource.getData().length(), TB_RESOURCE);
        }
    }

    @Override
    protected void validateDataImpl(TenantId tenantId, TbResource resource) {
        if (StringUtils.isEmpty(resource.getTitle())) {
            throw new DataValidationException("Resource title should be specified!");
        }
        if (resource.getResourceType() == null) {
            throw new DataValidationException("Resource type should be specified!");
        }
        if (StringUtils.isEmpty(resource.getFileName())) {
            throw new DataValidationException("Resource file name should be specified!");
        }
        if (StringUtils.isEmpty(resource.getResourceKey())) {
            throw new DataValidationException("Resource key should be specified!");
        }
        if (resource.getTenantId() == null) {
            resource.setTenantId(TenantId.fromUUID(ModelConstants.NULL_UUID));
        }
        if (!resource.getTenantId().getId().equals(ModelConstants.NULL_UUID)) {
            if (!tenantService.tenantExists(resource.getTenantId())) {
                throw new DataValidationException("Resource is referencing to non-existent tenant!");
            }
        }
    }
}
