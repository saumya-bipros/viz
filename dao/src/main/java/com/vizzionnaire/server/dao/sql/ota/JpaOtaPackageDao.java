package com.vizzionnaire.server.dao.sql.ota;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.OtaPackage;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.model.sql.OtaPackageEntity;
import com.vizzionnaire.server.dao.ota.OtaPackageDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractSearchTextDao;

import java.util.UUID;

@Slf4j
@Component
public class JpaOtaPackageDao extends JpaAbstractSearchTextDao<OtaPackageEntity, OtaPackage> implements OtaPackageDao {

    @Autowired
    private OtaPackageRepository otaPackageRepository;

    @Override
    protected Class<OtaPackageEntity> getEntityClass() {
        return OtaPackageEntity.class;
    }

    @Override
    protected JpaRepository<OtaPackageEntity, UUID> getRepository() {
        return otaPackageRepository;
    }

    @Override
    public Long sumDataSizeByTenantId(TenantId tenantId) {
        return otaPackageRepository.sumDataSizeByTenantId(tenantId.getId());
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.OTA_PACKAGE;
    }

}
