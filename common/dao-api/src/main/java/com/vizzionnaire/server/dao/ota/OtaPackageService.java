package com.vizzionnaire.server.dao.ota;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.OtaPackage;
import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.OtaPackageId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.ota.ChecksumAlgorithm;
import com.vizzionnaire.server.common.data.ota.OtaPackageType;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;

import java.nio.ByteBuffer;

public interface OtaPackageService {

    OtaPackageInfo saveOtaPackageInfo(OtaPackageInfo otaPackageInfo, boolean isUrl);

    OtaPackage saveOtaPackage(OtaPackage otaPackage);

    String generateChecksum(ChecksumAlgorithm checksumAlgorithm, ByteBuffer data);

    OtaPackage findOtaPackageById(TenantId tenantId, OtaPackageId otaPackageId);

    OtaPackageInfo findOtaPackageInfoById(TenantId tenantId, OtaPackageId otaPackageId);

    ListenableFuture<OtaPackageInfo> findOtaPackageInfoByIdAsync(TenantId tenantId, OtaPackageId otaPackageId);

    PageData<OtaPackageInfo> findTenantOtaPackagesByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<OtaPackageInfo> findTenantOtaPackagesByTenantIdAndDeviceProfileIdAndTypeAndHasData(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType, PageLink pageLink);

    void deleteOtaPackage(TenantId tenantId, OtaPackageId otaPackageId);

    void deleteOtaPackagesByTenantId(TenantId tenantId);

    long sumDataSizeByTenantId(TenantId tenantId);
}
