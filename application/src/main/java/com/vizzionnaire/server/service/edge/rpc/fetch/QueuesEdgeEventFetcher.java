package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.queue.Queue;
import com.vizzionnaire.server.dao.queue.QueueService;

@AllArgsConstructor
@Slf4j
public class QueuesEdgeEventFetcher extends BasePageableEdgeEventFetcher<Queue> {

    private final QueueService queueService;

    @Override
    PageData<Queue> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return queueService.findQueuesByTenantId(tenantId, pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, Queue queue) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.QUEUE,
                EdgeEventActionType.ADDED, queue.getId(), null);
    }
}
