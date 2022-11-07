package com.vizzionnaire.server.service.edge.rpc;

import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface EdgeRpcService {

    void updateEdge(TenantId tenantId, Edge edge);

    void deleteEdge(TenantId tenantId, EdgeId edgeId);

    void onEdgeEvent(TenantId tenantId, EdgeId edgeId);

    void startSyncProcess(TenantId tenantId, EdgeId edgeId);
}
