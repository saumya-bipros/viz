package com.vizzionnaire.server.dao.sql.device;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.device.DeviceCredentialsDao;
import com.vizzionnaire.server.dao.model.sql.DeviceCredentialsEntity;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import java.util.UUID;

/**
 * Created by Valerii Sosliuk on 5/6/2017.
 */
@Component
public class JpaDeviceCredentialsDao extends JpaAbstractDao<DeviceCredentialsEntity, DeviceCredentials> implements DeviceCredentialsDao {

    @Autowired
    private DeviceCredentialsRepository deviceCredentialsRepository;

    @Override
    protected Class<DeviceCredentialsEntity> getEntityClass() {
        return DeviceCredentialsEntity.class;
    }

    @Override
    protected JpaRepository<DeviceCredentialsEntity, UUID> getRepository() {
        return deviceCredentialsRepository;
    }

    @Transactional
    @Override
    public DeviceCredentials saveAndFlush(TenantId tenantId, DeviceCredentials deviceCredentials) {
        DeviceCredentials result = save(tenantId, deviceCredentials);
        deviceCredentialsRepository.flush();
        return result;
    }

    @Override
    public DeviceCredentials findByDeviceId(TenantId tenantId, UUID deviceId) {
        return DaoUtil.getData(deviceCredentialsRepository.findByDeviceId(deviceId));
    }

    @Override
    public DeviceCredentials findByCredentialsId(TenantId tenantId, String credentialsId) {
        return DaoUtil.getData(deviceCredentialsRepository.findByCredentialsId(credentialsId));
    }
}
