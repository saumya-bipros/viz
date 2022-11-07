package com.vizzionnaire.server.service.entitiy.ota;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.OtaPackage;
import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.SaveOtaPackageInfoRequest;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.OtaPackageId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.ota.ChecksumAlgorithm;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.entitiy.AbstractTbEntityService;

import java.nio.ByteBuffer;

@Service
@TbCoreComponent
@AllArgsConstructor
@Slf4j
public class DefaultTbOtaPackageService extends AbstractTbEntityService implements TbOtaPackageService {

    private final OtaPackageService otaPackageService;

    @Override
    public OtaPackageInfo save(SaveOtaPackageInfoRequest saveOtaPackageInfoRequest, User user) throws VizzionnaireException {
        ActionType actionType = saveOtaPackageInfoRequest.getId() == null ? ActionType.ADDED : ActionType.UPDATED;
        TenantId tenantId = saveOtaPackageInfoRequest.getTenantId();
        try {
            OtaPackageInfo savedOtaPackageInfo = otaPackageService.saveOtaPackageInfo(new OtaPackageInfo(saveOtaPackageInfoRequest), saveOtaPackageInfoRequest.isUsesUrl());

            boolean sendToEdge = savedOtaPackageInfo.hasUrl() || savedOtaPackageInfo.isHasData();
            notificationEntityService.notifyCreateOrUpdateOrDelete(tenantId, null, savedOtaPackageInfo.getId(),
                    savedOtaPackageInfo, user, actionType, sendToEdge, null);

            return savedOtaPackageInfo;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.OTA_PACKAGE), saveOtaPackageInfoRequest,
                    actionType, user, e);
            throw e;
        }
    }

    @Override
    public OtaPackageInfo saveOtaPackageData(OtaPackageInfo otaPackageInfo, String checksum, ChecksumAlgorithm checksumAlgorithm,
                                             byte[] data, String filename, String contentType, User user) throws VizzionnaireException {
        TenantId tenantId = otaPackageInfo.getTenantId();
        OtaPackageId otaPackageId = otaPackageInfo.getId();
        try {
            if (StringUtils.isEmpty(checksum)) {
                checksum = otaPackageService.generateChecksum(checksumAlgorithm, ByteBuffer.wrap(data));
            }
            OtaPackage otaPackage = new OtaPackage(otaPackageId);
            otaPackage.setCreatedTime(otaPackageInfo.getCreatedTime());
            otaPackage.setTenantId(tenantId);
            otaPackage.setDeviceProfileId(otaPackageInfo.getDeviceProfileId());
            otaPackage.setType(otaPackageInfo.getType());
            otaPackage.setTitle(otaPackageInfo.getTitle());
            otaPackage.setVersion(otaPackageInfo.getVersion());
            otaPackage.setTag(otaPackageInfo.getTag());
            otaPackage.setAdditionalInfo(otaPackageInfo.getAdditionalInfo());
            otaPackage.setChecksumAlgorithm(checksumAlgorithm);
            otaPackage.setChecksum(checksum);
            otaPackage.setFileName(filename);
            otaPackage.setContentType(contentType);
            otaPackage.setData(ByteBuffer.wrap(data));
            otaPackage.setDataSize((long) data.length);
            OtaPackageInfo savedOtaPackage = otaPackageService.saveOtaPackage(otaPackage);
            notificationEntityService.notifyCreateOrUpdateOrDelete(tenantId, null, savedOtaPackage.getId(),
                    savedOtaPackage, user, ActionType.UPDATED, true, null);
            return savedOtaPackage;
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.OTA_PACKAGE), ActionType.UPDATED,
                    user, e, otaPackageId.toString());
            throw e;
        }
    }

    @Override
    public void delete(OtaPackageInfo otaPackageInfo, User user) throws VizzionnaireException {
        TenantId tenantId = otaPackageInfo.getTenantId();
        OtaPackageId otaPackageId = otaPackageInfo.getId();
        try {
            otaPackageService.deleteOtaPackage(tenantId, otaPackageId);
            notificationEntityService.notifyCreateOrUpdateOrDelete(tenantId, null, otaPackageId, otaPackageInfo,
                    user, ActionType.DELETED, true, null, otaPackageInfo.getId().toString());
        } catch (Exception e) {
            notificationEntityService.logEntityAction(tenantId, emptyId(EntityType.OTA_PACKAGE),
                    ActionType.DELETED, user, e, otaPackageId.toString());
            throw e;
        }
    }
}
