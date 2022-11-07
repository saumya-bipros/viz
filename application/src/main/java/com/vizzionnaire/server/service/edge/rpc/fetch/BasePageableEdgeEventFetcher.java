package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.page.SortOrder;

@Slf4j
public abstract class BasePageableEdgeEventFetcher<T> implements EdgeEventFetcher {

    @Override
    public PageLink getPageLink(int pageSize) {
        return new PageLink(pageSize);
    }

    @Override
    public PageData<EdgeEvent> fetchEdgeEvents(TenantId tenantId, Edge edge, PageLink pageLink) {
        log.trace("[{}] start fetching edge events [{}]", tenantId, edge.getId());
        PageData<T> pageData = fetchPageData(tenantId, edge, pageLink);
        List<EdgeEvent> result = new ArrayList<>();
        if (!pageData.getData().isEmpty()) {
            for (T entity : pageData.getData()) {
                result.add(constructEdgeEvent(tenantId, edge, entity));
            }
        }
        return new PageData<>(result, pageData.getTotalPages(), pageData.getTotalElements(), pageData.hasNext());
    }

    abstract PageData<T> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink);

    abstract EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, T entity);
}
