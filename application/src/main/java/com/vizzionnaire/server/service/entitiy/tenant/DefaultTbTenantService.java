package com.vizzionnaire.server.service.entitiy.tenant;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantProfileService;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;
import com.vizzionnaire.server.service.entitiy.queue.TbQueueService;
import com.vizzionnaire.server.service.install.InstallScripts;
import com.vizzionnaire.server.service.sync.vc.EntitiesVersionControlService;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class DefaultTbTenantService extends AbstractTbEntityService implements TbTenantService {

    private final TenantService tenantService;
    private final TbTenantProfileCache tenantProfileCache;
    private final InstallScripts installScripts;
    private final TbQueueService tbQueueService;
    private final TenantProfileService tenantProfileService;
    private final EntitiesVersionControlService versionControlService;

    @Override
    public Tenant save(Tenant tenant) throws Exception {
        boolean created = tenant.getId() == null;
        Tenant oldTenant = !created ? tenantService.findTenantById(tenant.getId()) : null;

        Tenant savedTenant = checkNotNull(tenantService.saveTenant(tenant));
        if (created) {
            installScripts.createDefaultRuleChains(savedTenant.getId());
            installScripts.createDefaultEdgeRuleChains(savedTenant.getId());
        }
        tenantProfileCache.evict(savedTenant.getId());
        notificationEntityService.notifyCreateOrUpdateTenant(savedTenant, created ?
                ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);

        TenantProfile oldTenantProfile = oldTenant != null ? tenantProfileService.findTenantProfileById(TenantId.SYS_TENANT_ID, oldTenant.getTenantProfileId()) : null;
        TenantProfile newTenantProfile = tenantProfileService.findTenantProfileById(TenantId.SYS_TENANT_ID, savedTenant.getTenantProfileId());
        tbQueueService.updateQueuesByTenants(Collections.singletonList(savedTenant.getTenantId()), newTenantProfile, oldTenantProfile);
        return savedTenant;
    }

    @Override
    public void delete(Tenant tenant) throws Exception {
        TenantId tenantId = tenant.getId();
        tenantService.deleteTenant(tenantId);
        tenantProfileCache.evict(tenantId);
        notificationEntityService.notifyDeleteTenant(tenant);
        versionControlService.deleteVersionControlSettings(tenantId).get(1, TimeUnit.MINUTES);
    }
}
