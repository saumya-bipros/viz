package com.vizzionnaire.server.service.entitiy.device.profile;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.dao.device.DeviceProfileService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;
import com.vizzionnaire.server.service.ota.OtaPackageStateService;

import java.util.Objects;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultTbDeviceProfileService extends AbstractTbEntityService implements TbDeviceProfileService {

    private final DeviceProfileService deviceProfileService;
    private final OtaPackageStateService otaPackageStateService;

    @Override
    public DeviceProfile save(DeviceProfile deviceProfile, User user) throws Exception {
        ActionType actionType = deviceProfile.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = deviceProfile.getTenantId();
        try {
            boolean isFirmwareChanged = false;
            boolean isSoftwareChanged = false;

            if (actionType.equals(ActionType.UPDATED)) {
                DeviceProfile oldDeviceProfile = deviceProfileService.findDeviceProfileById(tenantId, deviceProfile.getId());
                if (!Objects.equals(deviceProfile.getFirmwareId(), oldDeviceProfile.getFirmwareId())) {
                    isFirmwareChanged = true;
                }
                if (!Objects.equals(deviceProfile.getSoftwareId(), oldDeviceProfile.getSoftwareId())) {
                    isSoftwareChanged = true;
                }
            }
            DeviceProfile savedDeviceProfile = checkNotNull(deviceProfileService.saveDeviceProfile(deviceProfile));
            autoCommit(user, savedDeviceProfile.getId());
            tbClusterService.onDeviceProfileChange(savedDeviceProfile, null);
            tbClusterService.broadcastEntityStateChangeEvent(tenantId, savedDeviceProfile.getId(),
                    actionType.equals(ActionType.ADDED) ? ComponentLifecycleEvent.CREATED : ComponentLifecycleEvent.UPDATED);

            otaPackageStateService.update(savedDeviceProfile, isFirmwareChanged, isSoftwareChanged);

            notificationEntityService.notifyCreateOrUpdateOrDelete(tenantId, null, savedDeviceProfile.getId(),
                    savedDeviceProfile, user, actionType, true, null);
            return savedDeviceProfile;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE_PROFILE), deviceProfile, actionType, user, e);
            throw e;
        }
    }

    @Override
    public void delete(DeviceProfile deviceProfile, User user) {
        DeviceProfileId deviceProfileId = deviceProfile.getId();
        TenantId tenantId = deviceProfile.getTenantId();
        try {
            deviceProfileService.deleteDeviceProfile(tenantId, deviceProfileId);

            tbClusterService.onDeviceProfileDelete(deviceProfile, null);
            tbClusterService.broadcastEntityStateChangeEvent(tenantId, deviceProfileId, ComponentLifecycleEvent.DELETED);
            notificationEntityService.notifyCreateOrUpdateOrDelete(tenantId, null, deviceProfileId, deviceProfile,
                    user, ActionType.DELETED, true, null, deviceProfileId.toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE_PROFILE), ActionType.DELETED,
                    user, e, deviceProfileId.toString());
            throw e;
        }
    }

    @Override
    public DeviceProfile setDefaultDeviceProfile(DeviceProfile deviceProfile, DeviceProfile previousDefaultDeviceProfile, User user) throws VizzionnaireException {
        TenantId tenantId = deviceProfile.getTenantId();
        DeviceProfileId deviceProfileId = deviceProfile.getId();
        try {
            if (deviceProfileService.setDefaultDeviceProfile(tenantId, deviceProfileId)) {
                if (previousDefaultDeviceProfile != null) {
                    previousDefaultDeviceProfile = deviceProfileService.findDeviceProfileById(tenantId, previousDefaultDeviceProfile.getId());
                    notificationEntityService.logEntityAction(tenantId, previousDefaultDeviceProfile.getId(), previousDefaultDeviceProfile,
                            ActionType.UPDATED, user);
                }
                deviceProfile = deviceProfileService.findDeviceProfileById(tenantId, deviceProfileId);

                notificationEntityService.logEntityAction(tenantId, deviceProfileId, deviceProfile, ActionType.UPDATED, user);
            }
            return deviceProfile;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.DEVICE_PROFILE), ActionType.UPDATED,
                    user, e, deviceProfileId.toString());
            throw e;
        }
    }
}
