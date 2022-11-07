package com.vizzionnaire.server.service.edge;

import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.gen.transport.TransportProtos;

public interface EdgeNotificationService {

    Edge setEdgeRootRuleChain(TenantId tenantId, Edge edge, RuleChainId ruleChainId) throws Exception;

    void pushNotificationToEdge(TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg, TbCallback callback);
}
