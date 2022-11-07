package com.vizzionnaire.server.service.sync.ie.importing.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.WidgetsBundleId;
import com.vizzionnaire.server.common.data.sync.ie.WidgetsBundleExportData;
import com.vizzionnaire.server.common.data.widget.BaseWidgetType;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.common.data.widget.WidgetTypeInfo;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;
import com.vizzionnaire.server.dao.widget.WidgetTypeService;
import com.vizzionnaire.server.dao.widget.WidgetsBundleService;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.sync.vc.data.EntitiesImportCtx;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@TbCoreComponent
@RequiredArgsConstructor
public class WidgetsBundleImportService extends BaseEntityImportService<WidgetsBundleId, WidgetsBundle, WidgetsBundleExportData> {

    private final WidgetsBundleService widgetsBundleService;
    private final WidgetTypeService widgetTypeService;

    @Override
    protected void setOwner(TenantId tenantId, WidgetsBundle widgetsBundle, IdProvider idProvider) {
        widgetsBundle.setTenantId(tenantId);
    }

    @Override
    protected WidgetsBundle prepare(EntitiesImportCtx ctx, WidgetsBundle widgetsBundle, WidgetsBundle old, WidgetsBundleExportData exportData, IdProvider idProvider) {
        return widgetsBundle;
    }

    @Override
    protected WidgetsBundle saveOrUpdate(EntitiesImportCtx ctx, WidgetsBundle widgetsBundle, WidgetsBundleExportData exportData, IdProvider idProvider) {
        WidgetsBundle savedWidgetsBundle = widgetsBundleService.saveWidgetsBundle(widgetsBundle);
        if (widgetsBundle.getId() == null) {
            for (WidgetTypeDetails widget : exportData.getWidgets()) {
                widget.setId(null);
                widget.setTenantId(ctx.getTenantId());
                widget.setBundleAlias(savedWidgetsBundle.getAlias());
                widgetTypeService.saveWidgetType(widget);
            }
        } else {
            Map<String, WidgetTypeInfo> existingWidgets = widgetTypeService.findWidgetTypesInfosByTenantIdAndBundleAlias(ctx.getTenantId(), savedWidgetsBundle.getAlias()).stream()
                    .collect(Collectors.toMap(BaseWidgetType::getAlias, w -> w));
            for (WidgetTypeDetails widget : exportData.getWidgets()) {
                WidgetTypeInfo existingWidget;
                if ((existingWidget = existingWidgets.remove(widget.getAlias())) != null) {
                    widget.setId(existingWidget.getId());
                    widget.setCreatedTime(existingWidget.getCreatedTime());
                } else {
                    widget.setId(null);
                }
                widget.setTenantId(ctx.getTenantId());
                widget.setBundleAlias(savedWidgetsBundle.getAlias());
                widgetTypeService.saveWidgetType(widget);
            }
            existingWidgets.values().stream()
                    .map(BaseWidgetType::getId)
                    .forEach(widgetTypeId -> widgetTypeService.deleteWidgetType(ctx.getTenantId(), widgetTypeId));
        }
        return savedWidgetsBundle;
    }

    @Override
    protected boolean compare(EntitiesImportCtx ctx, WidgetsBundleExportData exportData, WidgetsBundle prepared, WidgetsBundle existing) {
        return true;
    }

    @Override
    protected void onEntitySaved(User user, WidgetsBundle savedWidgetsBundle, WidgetsBundle oldWidgetsBundle) throws VizzionnaireException {
        entityNotificationService.notifySendMsgToEdgeService(user.getTenantId(), savedWidgetsBundle.getId(),
                oldWidgetsBundle == null ? EdgeEventActionType.ADDED : EdgeEventActionType.UPDATED);
    }

    @Override
    protected WidgetsBundle deepCopy(WidgetsBundle widgetsBundle) {
        return new WidgetsBundle(widgetsBundle);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.WIDGETS_BUNDLE;
    }

}
