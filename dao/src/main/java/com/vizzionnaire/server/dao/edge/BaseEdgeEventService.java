package com.vizzionnaire.server.dao.edge;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.TimePageLink;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.dao.service.DataValidator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BaseEdgeEventService implements EdgeEventService {

    @Autowired
    private EdgeEventDao edgeEventDao;

    @Autowired
    private DataValidator<EdgeEvent> edgeEventValidator;

    @Override
    public ListenableFuture<Void> saveAsync(EdgeEvent edgeEvent) {
        edgeEventValidator.validate(edgeEvent, EdgeEvent::getTenantId);
        return edgeEventDao.saveAsync(edgeEvent);
    }

    @Override
    public PageData<EdgeEvent> findEdgeEvents(TenantId tenantId, EdgeId edgeId, TimePageLink pageLink, boolean withTsUpdate) {
        return edgeEventDao.findEdgeEvents(tenantId.getId(), edgeId, pageLink, withTsUpdate);
    }

    @Override
    public void cleanupEvents(long ttl) {
        edgeEventDao.cleanupEvents(ttl);
    }
}
