package com.vizzionnaire.server.service.ota;

import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToOtaPackageStateServiceMsg;

public interface OtaPackageStateService {

    void update(Device device, Device oldDevice);

    void update(DeviceProfile deviceProfile, boolean isFirmwareChanged, boolean isSoftwareChanged);

    boolean process(ToOtaPackageStateServiceMsg msg);

}
