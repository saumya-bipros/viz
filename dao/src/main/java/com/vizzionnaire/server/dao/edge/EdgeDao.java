package com.vizzionnaire.server.dao.edge;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.EntitySubtype;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeInfo;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.Dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The Interface EdgeDao.
 *
 */
public interface EdgeDao extends Dao<Edge> {

    /**
     * Save or update edge object
     *
     * @param edge the edge object
     * @return saved edge object
     */
    Edge save(TenantId tenantId, Edge edge);

    /**
     * Find edge info by id.
     *
     * @param tenantId the tenant id
     * @param edgeId the edge id
     * @return the edge info object
     */
    EdgeInfo findEdgeInfoById(TenantId tenantId, UUID edgeId);

    /**
     * Find edges by tenantId and page link.
     *
     * @param tenantId the tenantId
     * @param pageLink the page link
     * @return the list of edge objects
     */
    PageData<Edge> findEdgesByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find edges by tenantId, type and page link.
     *
     * @param tenantId the tenantId
     * @param type the type
     * @param pageLink the page link
     * @return the list of edge objects
     */
    PageData<Edge> findEdgesByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    /**
     * Find edges by tenantId and edges Ids.
     *
     * @param tenantId the tenantId
     * @param edgeIds the edge Ids
     * @return the list of edge objects
     */
    ListenableFuture<List<Edge>> findEdgesByTenantIdAndIdsAsync(UUID tenantId, List<UUID> edgeIds);

    /**
     * Find edges by tenantId, customerId and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of edge objects
     */
    PageData<Edge> findEdgesByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    /**
     * Find edges by tenantId, customerId, type and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param type the type
     * @param pageLink the page link
     * @return the list of edge objects
     */
    PageData<Edge> findEdgesByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    /**
     * Find edge infos by tenantId, customerId and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param pageLink the page link
     * @return the list of edge info objects
     */
    PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerId(UUID tenantId, UUID customerId, PageLink pageLink);

    /**
     * Find edge infos by tenantId, customerId, type and page link.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param type the type
     * @param pageLink the page link
     * @return the list of edge info objects
     */
    PageData<EdgeInfo> findEdgeInfosByTenantIdAndCustomerIdAndType(UUID tenantId, UUID customerId, String type, PageLink pageLink);

    /**
     * Find edges by tenantId, customerId and edges Ids.
     *
     * @param tenantId the tenantId
     * @param customerId the customerId
     * @param edgeIds the edge Ids
     * @return the list of edge objects
     */
    ListenableFuture<List<Edge>> findEdgesByTenantIdCustomerIdAndIdsAsync(UUID tenantId, UUID customerId, List<UUID> edgeIds);

    /**
     * Find edges by tenantId and edge name.
     *
     * @param tenantId the tenantId
     * @param name the edge name
     * @return the optional edge object
     */
    Optional<Edge> findEdgeByTenantIdAndName(UUID tenantId, String name);

    /**
     * Find tenants edge types.
     *
     * @return the list of tenant edge type objects
     */
    ListenableFuture<List<EntitySubtype>> findTenantEdgeTypesAsync(UUID tenantId);

    /**
     * Find edge by routing Key.
     *
     * @param routingKey the edge routingKey
     * @return the optional edge object
     */
    Optional<Edge> findByRoutingKey(UUID tenantId, String routingKey);

    PageData<EdgeInfo> findEdgeInfosByTenantIdAndType(UUID tenantId, String type, PageLink pageLink);

    PageData<EdgeInfo> findEdgeInfosByTenantId(UUID tenantId, PageLink pageLink);

    /**
     * Find edges by tenantId and entityId.
     *
     * @param tenantId the tenantId
     * @param entityId the entityId
     * @param entityType the entityType
     * @return the list of edge objects
     */
    PageData<Edge> findEdgesByTenantIdAndEntityId(UUID tenantId, UUID entityId, EntityType entityType, PageLink pageLink);

}