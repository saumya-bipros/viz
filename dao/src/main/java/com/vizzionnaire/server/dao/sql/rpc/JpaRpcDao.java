package com.vizzionnaire.server.dao.sql.rpc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rpc.Rpc;
import com.vizzionnaire.server.common.data.rpc.RpcStatus;
import com.vizzionnaire.server.dao.DaoUtil;
import com.vizzionnaire.server.dao.model.sql.RpcEntity;
import com.vizzionnaire.server.dao.rpc.RpcDao;
import com.vizzionnaire.server.dao.sql.JpaAbstractDao;

import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class JpaRpcDao extends JpaAbstractDao<RpcEntity, Rpc> implements RpcDao {

    private final RpcRepository rpcRepository;

    @Override
    protected Class<RpcEntity> getEntityClass() {
        return RpcEntity.class;
    }

    @Override
    protected JpaRepository<RpcEntity, UUID> getRepository() {
        return rpcRepository;
    }

    @Override
    public PageData<Rpc> findAllByDeviceId(TenantId tenantId, DeviceId deviceId, PageLink pageLink) {
        return DaoUtil.toPageData(rpcRepository.findAllByTenantIdAndDeviceId(tenantId.getId(), deviceId.getId(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Rpc> findAllByDeviceIdAndStatus(TenantId tenantId, DeviceId deviceId, RpcStatus rpcStatus, PageLink pageLink) {
        return DaoUtil.toPageData(rpcRepository.findAllByTenantIdAndDeviceIdAndStatus(tenantId.getId(), deviceId.getId(), rpcStatus, DaoUtil.toPageable(pageLink)));
    }

    @Override
    public PageData<Rpc> findAllRpcByTenantId(TenantId tenantId, PageLink pageLink) {
        return DaoUtil.toPageData(rpcRepository.findAllByTenantId(tenantId.getId(), DaoUtil.toPageable(pageLink)));
    }

    @Override
    public Long deleteOutdatedRpcByTenantId(TenantId tenantId, Long expirationTime) {
        return rpcRepository.deleteOutdatedRpcByTenantId(tenantId.getId(), expirationTime);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.RPC;
    }

}
