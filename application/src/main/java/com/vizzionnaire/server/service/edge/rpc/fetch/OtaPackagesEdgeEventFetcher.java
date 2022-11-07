package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.ota.OtaPackageService;

@AllArgsConstructor
@Slf4j
public class OtaPackagesEdgeEventFetcher extends BasePageableEdgeEventFetcher<OtaPackageInfo> {

    private final OtaPackageService otaPackageService;

    @Override
    PageData<OtaPackageInfo> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return otaPackageService.findTenantOtaPackagesByTenantId(tenantId, pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, OtaPackageInfo otaPackageInfo) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.OTA_PACKAGE,
                EdgeEventActionType.ADDED, otaPackageInfo.getId(), null);
    }
}
