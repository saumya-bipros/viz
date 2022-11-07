package com.vizzionnaire.server.service.edge.rpc;

import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.service.edge.EdgeContextComponent;
import com.vizzionnaire.server.service.edge.rpc.fetch.AdminSettingsEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.AssetsEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.CustomerEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.CustomerUsersEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.DashboardsEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.DeviceProfilesEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.EdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.OtaPackagesEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.QueuesEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.RuleChainsEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.SystemWidgetsBundlesEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.TenantAdminUsersEdgeEventFetcher;
import com.vizzionnaire.server.service.edge.rpc.fetch.TenantWidgetsBundlesEdgeEventFetcher;

import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

public class EdgeSyncCursor {

    List<EdgeEventFetcher> fetchers = new LinkedList<>();

    int currentIdx = 0;

    public EdgeSyncCursor(EdgeContextComponent ctx, Edge edge) {
        fetchers.add(new QueuesEdgeEventFetcher(ctx.getQueueService()));
        fetchers.add(new RuleChainsEdgeEventFetcher(ctx.getRuleChainService()));
        fetchers.add(new AdminSettingsEdgeEventFetcher(ctx.getAdminSettingsService(), ctx.getFreemarkerConfig()));
        fetchers.add(new DeviceProfilesEdgeEventFetcher(ctx.getDeviceProfileService()));
        fetchers.add(new TenantAdminUsersEdgeEventFetcher(ctx.getUserService()));
        if (edge.getCustomerId() != null && !EntityId.NULL_UUID.equals(edge.getCustomerId().getId())) {
            fetchers.add(new CustomerEdgeEventFetcher());
            fetchers.add(new CustomerUsersEdgeEventFetcher(ctx.getUserService(), edge.getCustomerId()));
        }
        fetchers.add(new AssetsEdgeEventFetcher(ctx.getAssetService()));
        fetchers.add(new SystemWidgetsBundlesEdgeEventFetcher(ctx.getWidgetsBundleService()));
        fetchers.add(new TenantWidgetsBundlesEdgeEventFetcher(ctx.getWidgetsBundleService()));
        fetchers.add(new DashboardsEdgeEventFetcher(ctx.getDashboardService()));
        fetchers.add(new OtaPackagesEdgeEventFetcher(ctx.getOtaPackageService()));
    }

    public boolean hasNext() {
        return fetchers.size() > currentIdx;
    }

    public EdgeEventFetcher getNext() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        EdgeEventFetcher edgeEventFetcher = fetchers.get(currentIdx);
        currentIdx++;
        return edgeEventFetcher;
    }

    public int getCurrentIdx() {
        return currentIdx;
    }
}
