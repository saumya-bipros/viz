package com.vizzionnaire.server.dao.rpc;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.RpcId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rpc.Rpc;
import com.vizzionnaire.server.common.data.rpc.RpcStatus;

public interface RpcService {
    Rpc save(Rpc rpc);

    void deleteRpc(TenantId tenantId, RpcId id);

    void deleteAllRpcByTenantId(TenantId tenantId);

    Rpc findById(TenantId tenantId, RpcId id);

    ListenableFuture<Rpc> findRpcByIdAsync(TenantId tenantId, RpcId id);

    PageData<Rpc> findAllByDeviceId(TenantId tenantId, DeviceId deviceId, PageLink pageLink);

    PageData<Rpc> findAllByDeviceIdAndStatus(TenantId tenantId, DeviceId deviceId, RpcStatus rpcStatus, PageLink pageLink);
}
