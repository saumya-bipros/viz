package com.vizzionnaire.server.service.entitiy.ota;

import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.SaveOtaPackageInfoRequest;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.ota.ChecksumAlgorithm;

public interface TbOtaPackageService {

    OtaPackageInfo save(SaveOtaPackageInfoRequest saveOtaPackageInfoRequest, User user) throws ThingsboardException;

    OtaPackageInfo saveOtaPackageData(OtaPackageInfo otaPackageInfo, String checksum, ChecksumAlgorithm checksumAlgorithm,
                                      byte[] data, String filename, String contentType, User user) throws ThingsboardException;

    void delete(OtaPackageInfo otaPackageInfo, User user) throws ThingsboardException;

}
