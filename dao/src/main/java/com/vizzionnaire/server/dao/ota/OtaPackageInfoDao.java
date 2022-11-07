package com.vizzionnaire.server.dao.ota;

import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.OtaPackageId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.ota.OtaPackageType;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.Dao;

public interface OtaPackageInfoDao extends Dao<OtaPackageInfo> {

    PageData<OtaPackageInfo> findOtaPackageInfoByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<OtaPackageInfo> findOtaPackageInfoByTenantIdAndDeviceProfileIdAndTypeAndHasData(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType, PageLink pageLink);

    boolean isOtaPackageUsed(OtaPackageId otaPackageId, OtaPackageType otaPackageType, DeviceProfileId deviceProfileId);

}
