package com.vizzionnaire.server.dao.widget;

import java.util.List;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.WidgetTypeId;
import com.vizzionnaire.server.common.data.widget.WidgetType;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.common.data.widget.WidgetTypeInfo;

public interface WidgetTypeService {

    WidgetType findWidgetTypeById(TenantId tenantId, WidgetTypeId widgetTypeId);

    WidgetTypeDetails findWidgetTypeDetailsById(TenantId tenantId, WidgetTypeId widgetTypeId);

    WidgetTypeDetails saveWidgetType(WidgetTypeDetails widgetType);

    void deleteWidgetType(TenantId tenantId, WidgetTypeId widgetTypeId);

    List<WidgetType> findWidgetTypesByTenantIdAndBundleAlias(TenantId tenantId, String bundleAlias);

    List<WidgetTypeDetails> findWidgetTypesDetailsByTenantIdAndBundleAlias(TenantId tenantId, String bundleAlias);

    List<WidgetTypeInfo> findWidgetTypesInfosByTenantIdAndBundleAlias(TenantId tenantId, String bundleAlias);

    WidgetType findWidgetTypeByTenantIdBundleAliasAndAlias(TenantId tenantId, String bundleAlias, String alias);

    void deleteWidgetTypesByTenantIdAndBundleAlias(TenantId tenantId, String bundleAlias);

}
