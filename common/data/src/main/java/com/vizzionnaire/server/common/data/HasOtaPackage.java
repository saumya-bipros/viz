package com.vizzionnaire.server.common.data;

import com.vizzionnaire.server.common.data.id.OtaPackageId;

public interface HasOtaPackage {

    OtaPackageId getFirmwareId();

    OtaPackageId getSoftwareId();
}
