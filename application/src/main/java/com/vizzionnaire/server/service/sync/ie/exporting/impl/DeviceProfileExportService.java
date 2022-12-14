package com.vizzionnaire.server.service.sync.ie.exporting.impl;

import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesExportCtx;

import java.util.Set;

@Service
@TbCoreComponent
public class DeviceProfileExportService extends BaseEntityExportService<DeviceProfileId, DeviceProfile, EntityExportData<DeviceProfile>> {

    @Override
    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, DeviceProfile deviceProfile, EntityExportData<DeviceProfile> exportData) {
        deviceProfile.setDefaultDashboardId(getExternalIdOrElseInternal(ctx, deviceProfile.getDefaultDashboardId()));
        deviceProfile.setDefaultRuleChainId(getExternalIdOrElseInternal(ctx, deviceProfile.getDefaultRuleChainId()));
    }

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return Set.of(EntityType.DEVICE_PROFILE);
    }

}
