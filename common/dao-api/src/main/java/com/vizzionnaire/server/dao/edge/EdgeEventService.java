package com.vizzionnaire.server.dao.edge;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.TimePageLink;

public interface EdgeEventService {

    ListenableFuture<Void> saveAsync(EdgeEvent edgeEvent);

    PageData<EdgeEvent> findEdgeEvents(TenantId tenantId, EdgeId edgeId, TimePageLink pageLink, boolean withTsUpdate);

    /**
     * Executes stored procedure to cleanup old edge events.
     * @param ttl the ttl for edge events in seconds
     */
    void cleanupEvents(long ttl);
}
