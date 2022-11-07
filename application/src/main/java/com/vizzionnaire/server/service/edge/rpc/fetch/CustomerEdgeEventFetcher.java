package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;

@AllArgsConstructor
@Slf4j
public class CustomerEdgeEventFetcher implements EdgeEventFetcher {

    @Override
    public PageLink getPageLink(int pageSize) {
        return null;
    }

    @Override
    public PageData<EdgeEvent> fetchEdgeEvents(TenantId tenantId, Edge edge, PageLink pageLink) {
        List<EdgeEvent> result = new ArrayList<>();
        result.add(EdgeUtils.constructEdgeEvent(edge.getTenantId(), edge.getId(),
                EdgeEventType.CUSTOMER, EdgeEventActionType.ADDED, edge.getCustomerId(), null));
        // @voba - returns PageData object to be in sync with other fetchers
        return new PageData<>(result, 1, result.size(), false);
    }
}
