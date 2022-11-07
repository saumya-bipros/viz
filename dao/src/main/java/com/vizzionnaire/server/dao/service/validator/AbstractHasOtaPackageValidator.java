package com.vizzionnaire.server.dao.service.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.vizzionnaire.server.common.data.BaseData;
import com.vizzionnaire.server.common.data.HasOtaPackage;
import com.vizzionnaire.server.common.data.OtaPackage;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.ota.OtaPackageType;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.service.DataValidator;

public abstract class AbstractHasOtaPackageValidator<D extends BaseData<?>> extends DataValidator<D> {

    @Autowired
    @Lazy
    private OtaPackageService otaPackageService;

    protected <T extends HasOtaPackage> void validateOtaPackage(TenantId tenantId, T entity, DeviceProfileId deviceProfileId) {
        if (entity.getFirmwareId() != null) {
            OtaPackage firmware = otaPackageService.findOtaPackageById(tenantId, entity.getFirmwareId());
            validateOtaPackage(tenantId, OtaPackageType.FIRMWARE, deviceProfileId, firmware);
        }
        if (entity.getSoftwareId() != null) {
            OtaPackage software = otaPackageService.findOtaPackageById(tenantId, entity.getSoftwareId());
            validateOtaPackage(tenantId, OtaPackageType.SOFTWARE, deviceProfileId, software);
        }
    }

    private void validateOtaPackage(TenantId tenantId, OtaPackageType type, DeviceProfileId deviceProfileId, OtaPackage otaPackage) {
        if (otaPackage == null) {
            throw new DataValidationException(prepareMsg("Can't assign non-existent %s!", type));
        }
        if (!otaPackage.getTenantId().equals(tenantId)) {
            throw new DataValidationException(prepareMsg("Can't assign %s from different tenant!", type));
        }
        if (!otaPackage.getType().equals(type)) {
            throw new DataValidationException(prepareMsg("Can't assign %s with type: " + otaPackage.getType(), type));
        }
        if (otaPackage.getData() == null && !otaPackage.hasUrl()) {
            throw new DataValidationException(prepareMsg("Can't assign %s with empty data!", type));
        }
        if (!otaPackage.getDeviceProfileId().equals(deviceProfileId)) {
            throw new DataValidationException(prepareMsg("Can't assign %s with different deviceProfile!", type));
        }
    }

    private String prepareMsg(String msg, OtaPackageType type) {
        return String.format(msg, type.name().toLowerCase());
    }
}
