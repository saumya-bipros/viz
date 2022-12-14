package com.vizzionnaire.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.sync.ie.DeviceExportData;
import com.vizzionnaire.server.dao.device.DeviceCredentialsService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesImportCtx;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class DeviceImportService extends BaseEntityImportService<DeviceId, Device, DeviceExportData> {

    private final DeviceService deviceService;
    private final DeviceCredentialsService credentialsService;

    @Override
    protected void setOwner(TenantId tenantId, Device device, IdProvider idProvider) {
        device.setTenantId(tenantId);
        device.setCustomerId(idProvider.getInternalId(device.getCustomerId()));
    }

    @Override
    protected Device prepare(EntitiesImportCtx ctx, Device device, Device old, DeviceExportData exportData, IdProvider idProvider) {
        device.setDeviceProfileId(idProvider.getInternalId(device.getDeviceProfileId()));
        device.setFirmwareId(getOldEntityField(old, Device::getFirmwareId));
        device.setSoftwareId(getOldEntityField(old, Device::getSoftwareId));
        return device;
    }

    @Override
    protected Device deepCopy(Device d) {
        return new Device(d);
    }

    @Override
    protected void cleanupForComparison(Device e) {
        super.cleanupForComparison(e);
        if (e.getCustomerId() != null && e.getCustomerId().isNullUid()) {
            e.setCustomerId(null);
        }
    }

    @Override
    protected Device saveOrUpdate(EntitiesImportCtx ctx, Device device, DeviceExportData exportData, IdProvider idProvider) {
        if (exportData.getCredentials() != null && ctx.isSaveCredentials()) {
            exportData.getCredentials().setId(null);
            exportData.getCredentials().setDeviceId(null);
            return deviceService.saveDeviceWithCredentials(device, exportData.getCredentials());
        } else {
            return deviceService.saveDevice(device);
        }
    }

    @Override
    protected boolean updateRelatedEntitiesIfUnmodified(EntitiesImportCtx ctx, Device prepared, DeviceExportData exportData, IdProvider idProvider) {
        boolean updated = super.updateRelatedEntitiesIfUnmodified(ctx, prepared, exportData, idProvider);
        var credentials = exportData.getCredentials();
        if (credentials != null && ctx.isSaveCredentials()) {
            var existing = credentialsService.findDeviceCredentialsByDeviceId(ctx.getTenantId(), prepared.getId());
            credentials.setId(existing.getId());
            credentials.setDeviceId(prepared.getId());
            if (!existing.equals(credentials)) {
                credentialsService.updateDeviceCredentials(ctx.getTenantId(), credentials);
                updated = true;
            }
        }
        return updated;
    }

    @Override
    protected void onEntitySaved(User user, Device savedDevice, Device oldDevice) throws VizzionnaireException {
        entityNotificationService.notifyCreateOrUpdateDevice(user.getTenantId(), savedDevice.getId(), savedDevice.getCustomerId(),
                savedDevice, oldDevice, oldDevice == null ? ActionType.ADDED : ActionType.UPDATED, user);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.DEVICE;
    }

}
