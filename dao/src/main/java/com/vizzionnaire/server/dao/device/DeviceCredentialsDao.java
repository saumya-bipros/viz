package com.vizzionnaire.server.dao.device;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.dao.Dao;

import java.util.UUID;

/**
 * The Interface DeviceCredentialsDao.
 */
public interface DeviceCredentialsDao extends Dao<DeviceCredentials> {

    /**
     * Save or update device credentials object
     *
     * @param tenantId the device tenant id
     * @param deviceCredentials the device credentials object
     * @return saved device credentials object
     */
    DeviceCredentials save(TenantId tenantId, DeviceCredentials deviceCredentials);

    DeviceCredentials saveAndFlush(TenantId tenantId, DeviceCredentials deviceCredentials);

    /**
     * Find device credentials by device id.
     *
     * @param deviceId the device id
     * @return the device credentials object
     */
    DeviceCredentials findByDeviceId(TenantId tenantId, UUID deviceId);

    /**
     * Find device credentials by credentials id.
     *
     * @param credentialsId the credentials id
     * @return the device credentials object
     */
    DeviceCredentials findByCredentialsId(TenantId tenantId, String credentialsId);

}
