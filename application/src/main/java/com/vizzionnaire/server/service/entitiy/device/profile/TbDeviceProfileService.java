package com.vizzionnaire.server.service.entitiy.device.profile;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.service.entitiy.SimpleTbEntityService;

public interface TbDeviceProfileService extends SimpleTbEntityService<DeviceProfile> {

    DeviceProfile setDefaultDeviceProfile(DeviceProfile deviceProfile, DeviceProfile previousDefaultDeviceProfile, User user) throws VizzionnaireException;
}
