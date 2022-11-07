package com.vizzionnaire.server.service.entitiy.tenant.profile;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantProfileService;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;
import com.vizzionnaire.server.service.entitiy.queue.TbQueueService;

import java.util.List;

@Slf4j
@Service
@TbCoreComponent
@AllArgsConstructor
public class DefaultTbTenantProfileService extends AbstractTbEntityService implements TbTenantProfileService {
    private final TbQueueService tbQueueService;
    private final TenantProfileService tenantProfileService;
    private final TenantService tenantService;
    private final TbTenantProfileCache tenantProfileCache;

    @Override
    public TenantProfile save(TenantId tenantId, TenantProfile tenantProfile, TenantProfile oldTenantProfile) throws VizzionnaireException {
        TenantProfile savedTenantProfile = checkNotNull(tenantProfileService.saveTenantProfile(tenantId, tenantProfile));
        if (oldTenantProfile != null && savedTenantProfile.isIsolatedTbRuleEngine()) {
            List<TenantId> tenantIds = tenantService.findTenantIdsByTenantProfileId(savedTenantProfile.getId());
            tbQueueService.updateQueuesByTenants(tenantIds, savedTenantProfile, oldTenantProfile);
        }

        tenantProfileCache.put(savedTenantProfile);
        tbClusterService.onTenantProfileChange(savedTenantProfile, null);
        tbClusterService.broadcastEntityStateChangeEvent(TenantId.SYS_TENANT_ID, savedTenantProfile.getId(),
                tenantProfile.getId() == null ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);

        return savedTenantProfile;
    }

    @Override
    public void delete(TenantId tenantId, TenantProfile tenantProfile) throws VizzionnaireException {
        tenantProfileService.deleteTenantProfile(tenantId, tenantProfile.getId());
        tbClusterService.onTenantProfileDelete(tenantProfile, null);
    }
}
