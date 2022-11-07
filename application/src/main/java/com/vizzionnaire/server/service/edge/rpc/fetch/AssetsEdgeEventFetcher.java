package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.asset.AssetService;

@AllArgsConstructor
@Slf4j
public class AssetsEdgeEventFetcher extends BasePageableEdgeEventFetcher<Asset> {

    private final AssetService assetService;

    @Override
    PageData<Asset> fetchPageData(TenantId tenantId, Edge edge, PageLink pageLink) {
        return assetService.findAssetsByTenantIdAndEdgeId(tenantId, edge.getId(), pageLink);
    }

    @Override
    EdgeEvent constructEdgeEvent(TenantId tenantId, Edge edge, Asset asset) {
        return EdgeUtils.constructEdgeEvent(tenantId, edge.getId(), EdgeEventType.ASSET,
                EdgeEventActionType.ADDED, asset.getId(), null);
    }
}
