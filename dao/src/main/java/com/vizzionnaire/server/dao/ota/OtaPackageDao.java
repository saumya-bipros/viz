package com.vizzionnaire.server.dao.ota;

import com.vizzionnaire.server.common.data.OtaPackage;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.dao.Dao;
import com.vizzionnaire.server.dao.TenantEntityWithDataDao;

public interface OtaPackageDao extends Dao<OtaPackage>, TenantEntityWithDataDao {
    Long sumDataSizeByTenantId(TenantId tenantId);
}
