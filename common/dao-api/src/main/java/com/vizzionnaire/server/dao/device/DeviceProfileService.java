package com.vizzionnaire.server.dao.device;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.DeviceProfileInfo;
import com.vizzionnaire.server.common.data.EntityInfo;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;

public interface DeviceProfileService {

    DeviceProfile findDeviceProfileById(TenantId tenantId, DeviceProfileId deviceProfileId);

    DeviceProfile findDeviceProfileByName(TenantId tenantId, String profileName);

    DeviceProfileInfo findDeviceProfileInfoById(TenantId tenantId, DeviceProfileId deviceProfileId);

    DeviceProfile saveDeviceProfile(DeviceProfile deviceProfile);

    void deleteDeviceProfile(TenantId tenantId, DeviceProfileId deviceProfileId);

    PageData<DeviceProfile> findDeviceProfiles(TenantId tenantId, PageLink pageLink);

    PageData<DeviceProfileInfo> findDeviceProfileInfos(TenantId tenantId, PageLink pageLink, String transportType);

    DeviceProfile findOrCreateDeviceProfile(TenantId tenantId, String profileName);

    DeviceProfile createDefaultDeviceProfile(TenantId tenantId);

    DeviceProfile findDefaultDeviceProfile(TenantId tenantId);

    DeviceProfileInfo findDefaultDeviceProfileInfo(TenantId tenantId);

    boolean setDefaultDeviceProfile(TenantId tenantId, DeviceProfileId deviceProfileId);

    void deleteDeviceProfilesByTenantId(TenantId tenantId);

}
