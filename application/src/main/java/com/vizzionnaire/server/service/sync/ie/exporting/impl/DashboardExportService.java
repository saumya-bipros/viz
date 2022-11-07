package com.vizzionnaire.server.service.sync.ie.exporting.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.common.util.RegexUtils;
import com.vizzionnaire.server.common.data.Dashboard;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.DashboardId;
import com.vizzionnaire.server.common.data.sync.ie.EntityExportData;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesExportCtx;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Service
@TbCoreComponent
public class DashboardExportService extends BaseEntityExportService<DashboardId, Dashboard, EntityExportData<Dashboard>> {

    @Override
    protected void setRelatedEntities(EntitiesExportCtx<?> ctx, Dashboard dashboard, EntityExportData<Dashboard> exportData) {
        if (CollectionUtils.isNotEmpty(dashboard.getAssignedCustomers())) {
            dashboard.getAssignedCustomers().forEach(customerInfo -> {
                customerInfo.setCustomerId(getExternalIdOrElseInternal(ctx, customerInfo.getCustomerId()));
            });
        }
        for (JsonNode entityAlias : dashboard.getEntityAliasesConfig()) {
            replaceUuidsRecursively(ctx, entityAlias, Collections.emptySet());
        }
        for (JsonNode widgetConfig : dashboard.getWidgetsConfig()) {
            replaceUuidsRecursively(ctx, JacksonUtil.getSafely(widgetConfig, "config", "actions"), Collections.singleton("id"));
        }
    }

    @Override
    public Set<EntityType> getSupportedEntityTypes() {
        return Set.of(EntityType.DASHBOARD);
    }

}
