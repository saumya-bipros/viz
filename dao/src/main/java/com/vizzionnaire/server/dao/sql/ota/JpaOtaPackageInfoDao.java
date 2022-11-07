package com.vizzionnaire.server.dao.sql.ota;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.OtaPackageId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.ota.OtaPackageType;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.OtaPackageInfoEntity;
import com.vizzionnaire.server.dao.ota.OtaPackageInfoDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
public class JpaOtaPackageInfoDao extends JpaAbstractSearchTextDao<OtaPackageInfoEntity, OtaPackageInfo> implements OtaPackageInfoDao {

    @Autowired
    private OtaPackageInfoRepository otaPackageInfoRepository;

    @Override
    protected Class<OtaPackageInfoEntity> getEntityClass() {
        return OtaPackageInfoEntity.class;
    }

    @Override
    protected JpaRepository<OtaPackageInfoEntity, UUID> getRepository() {
        return otaPackageInfoRepository;
    }

    @Override
    public OtaPackageInfo findById(TenantId tenantId, UUID id) {
        return DaoUtil.getData(otaPackageInfoRepository.findOtaPackageInfoById(id));
    }

    @Override
    public OtaPackageInfo save(TenantId tenantId, OtaPackageInfo otaPackageInfo) {
        OtaPackageInfo savedOtaPackage = super.save(tenantId, otaPackageInfo);
        if (otaPackageInfo.getId() == null) {
            return savedOtaPackage;
        } else {
            return findById(tenantId, savedOtaPackage.getId().getId());
        }
    }

    @Override
    public PageData<OtaPackageInfo> findOtaPackageInfoByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(otaPackageInfoRepository
                .findAllByTenantId(
                        tenantId.getId(),
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<OtaPackageInfo> findOtaPackageInfoByTenantIdAndDeviceProfileIdAndTypeAndHasData(TenantId tenantId, DeviceProfileId deviceProfileId, OtaPackageType otaPackageType, PageLink pageLink) {
        return DaoUtil.toPageData(otaPackageInfoRepository
                .findAllByTenantIdAndTypeAndDeviceProfileIdAndHasData(
                        tenantId.getId(),
                        deviceProfileId.getId(),
                        otaPackageType,
                        Objects.toString(pageLink.getTextSearch(), ""),
                        DaoUtil.toPageable(pageLink)));
    }

    @Override
    public boolean isOtaPackageUsed(OtaPackageId otaPackageId, OtaPackageType otaPackageType, DeviceProfileId deviceProfileId) {
        return otaPackageInfoRepository.isOtaPackageUsed(otaPackageId.getId(), deviceProfileId.getId(), otaPackageType.name());
    }
}
