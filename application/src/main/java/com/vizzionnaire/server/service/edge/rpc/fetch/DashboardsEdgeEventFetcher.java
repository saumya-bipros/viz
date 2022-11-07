package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.DashboardInfo;
import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.dashboard.DashboardService;

@AllArgsConstructor
@Slf4j
public class DashboardsEdgeEventFetcher extends BasePageableEdgeEventFetcher<DashboardInfo> {

    private final DashboardService dashboardService;

    @Override
    PageData<DashboardInfo> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return dashboardService.findDashboardsByTenantIdAndEdgeId(tenantId, edge.getId(), pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, DashboardInfo dashboardInfo) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.DASHBOARD,
                EdgeEventActionType.ADDED, dashboardInfo.getId(), null);
    }
}
