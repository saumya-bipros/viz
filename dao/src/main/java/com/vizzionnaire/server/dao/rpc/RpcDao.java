package com.vizzionnaire.server.dao.rpc;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rpc.Rpc;
import com.vizzionnaire.server.common.data.rpc.RpcStatus;
import com.vizzionnaire.server.dao.Dao;

public interface RpcDao extends Dao<Rpc> {
    PageData<Rpc> findAllByDeviceId(TenantId tenantId, DeviceId deviceId, PageLink pageLink);

    PageData<Rpc> findAllByDeviceIdAndStatus(TenantId tenantId, DeviceId deviceId, RpcStatus rpcStatus, PageLink pageLink);

    PageData<Rpc> findAllRpcByTenantId(TenantId tenantId, PageLink pageLink);

    Long deleteOutdatedRpcByTenantId(TenantId tenantId, Long expirationTime);
}
