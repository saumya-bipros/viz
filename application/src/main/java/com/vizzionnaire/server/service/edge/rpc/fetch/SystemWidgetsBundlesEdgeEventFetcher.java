package com.vizzionnaire.server.service.edge.rpc.fetch;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;
import com.vizzionnaire.server.dao.widget.WidgetsBundleService;

@Slf4j
public class SystemWidgetsBundlesEdgeEventFetcher extends BaseWidgetsBundlesEdgeEventFetcher {

    public SystemWidgetsBundlesEdgeEventFetcher(WidgetsBundleService widgetsBundleService) {
        super(widgetsBundleService);
    }

    @Override
    protected PageData<WidgetsBundle> findWidgetsBundles(TenantId tenantId, PageLink pageLink) {
        return widgetsBundleService.findSystemWidgetsBundlesByPageLink(tenantId, pageLink);
    }
}
