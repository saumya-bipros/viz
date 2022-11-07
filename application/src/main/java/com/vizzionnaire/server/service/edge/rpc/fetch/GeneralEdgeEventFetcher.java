package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;

import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.page.SortOrder;
import com.vizzionnaire.server.common.data.page.TimePageLink;
import com.vizzionnaire.server.dao.edge.EdgeEventService;

@AllArgsConstructor
public class GeneralEdgeEventFetcher implements EdgeEventFetcher {

    private final Long queueStartTs;
    private final EdgeEventService edgeEventService;

    @Override
    public PageLink getPageLink(int pageSize) {
        return new TimePageLink(
                pageSize,
                0,
                null,
                new SortOrder("createdTime", SortOrder.Direction.ASC),
                queueStartTs,
                null);
    }

    @Override
    public PageData<EdgeEvent> fetchEdgeEvents(TenantId tenantId, Edge edge, PageLink pageLink) {
        return edgeEventService.findEdgeEvents(tenantId, edge.getId(), (TimePageLink) pageLink, true);
    }
}
