package com.vizzionnaire.server.dao.ota;

import com.vizzionnaire.server.common.data.id.OtaPackageId;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
class OtaPackageCacheEvictEvent {

    private final OtaPackageId id;

}
