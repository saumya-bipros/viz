package com.vizzionnaire.server.dao.widget;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.WidgetsBundleId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;
import com.vizzionnaire.server.dao.Dao;
import com.vizzionnaire.server.dao.ExportableEntityDao;

import java.util.UUID;

/**
 * The Interface WidgetsBundleDao.
 */
public interface WidgetsBundleDao extends Dao<WidgetsBundle>, ExportableEntityDao<WidgetsBundleId, WidgetsBundle> {

    /**
     * Save or update widgets bundle object
     *
     * @param tenantId the tenantId
     * @param widgetsBundle the widgets bundle object
     * @return saved widgets bundle object
     */
    WidgetsBundle save(TenantId tenantId, WidgetsBundle widgetsBundle);

    /**
     * Find widgets bundle by tenantId and alias.
     *
     * @param tenantId the tenantId
     * @param alias the alias
     * @return the widgets bundle object
     */
    WidgetsBundle findWidgetsBundleByTenantIdAndAlias(UUID tenantId, String alias);

    /**
     * Find system widgets bundles by page link.
     *
     * @param pageLink the page link
     * @return the list of widgets bundles objects
     */
    PageData<WidgetsBundle> findSystemWidgetsBundles(TenantId tenantId, PageLink pageLink);

    /**
     * Find tenant widgets bundles by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of widgets bundles objects
     */
    PageData<WidgetsBundle> findTenantWidgetsBundlesByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find all tenant widgets bundles (including system) by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of widgets bundles objects
     */
    PageData<WidgetsBundle> findAllTenantWidgetsBundlesByTenantId(UUID tenantId, PageLink pageLink);

}

