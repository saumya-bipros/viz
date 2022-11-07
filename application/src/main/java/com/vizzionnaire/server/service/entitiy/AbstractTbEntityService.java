package com.vizzionnaire.server.service.entitiy;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.alarm.AlarmInfo;
import com.vizzionnaire.server.common.data.alarm.AlarmQuery;
import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.AlarmId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageDataIterableByTenantIdEntityId;
import com.vizzionnaire.server.common.data.page.TimePageLink;
import com.vizzionnaire.server.dao.alarm.AlarmService;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.service.executors.DbCallbackExecutorService;
import com.vizzionnaire.server.service.sync.vc.EntitiesVersionControlService;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractTbEntityService {

    protected static final int DEFAULT_PAGE_SIZE = 1000;

    @Value("${server.log_controller_error_stack_trace}")
    @Getter
    private boolean logControllerErrorStackTrace;
    @Value("${edges.enabled}")
    @Getter
    protected boolean edgesEnabled;

    @Autowired
    protected DbCallbackExecutorService dbExecutor;
    @Autowired(required = false)
    protected TbNotificationEntityService notificationEntityService;
    @Autowired(required = false)
    protected EdgeService edgeService;
    @Autowired
    protected AlarmService alarmService;
    @Autowired
    protected CustomerService customerService;
    @Autowired
    protected TbClusterService tbClusterService;
    @Autowired(required = false)
    private EntitiesVersionControlService vcService;

    protected ListenableFuture<Void> removeAlarmsByEntityId(TenantId tenantId, EntityId entityId) {
        ListenableFuture<PageData<AlarmInfo>> alarmsFuture =
                alarmService.findAlarms(tenantId, new AlarmQuery(entityId, new TimePageLink(Integer.MAX_VALUE), null, null, false));

        ListenableFuture<List<AlarmId>> alarmIdsFuture = Futures.transform(alarmsFuture, page ->
                page.getData().stream().map(AlarmInfo::getId).collect(Collectors.toList()), dbExecutor);

        return Futures.transform(alarmIdsFuture, ids -> {
            ids.stream().map(alarmId -> alarmService.deleteAlarm(tenantId, alarmId)).collect(Collectors.toList());
            return null;
        }, dbExecutor);
    }

    protected <T> T checkNotNull(T reference) throws VizzionnaireException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    protected <T> T checkNotNull(T reference, String notFoundMessage) throws VizzionnaireException {
        if (reference == null) {
            throw new VizzionnaireException(notFoundMessage, VizzionnaireErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }

    protected <T> T checkNotNull(Optional<T> reference) throws VizzionnaireException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    protected <T> T checkNotNull(Optional<T> reference, String notFoundMessage) throws VizzionnaireException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new VizzionnaireException(notFoundMessage, VizzionnaireErrorCode.ITEM_NOT_FOUND);
        }
    }

    protected List<EdgeId> findRelatedEdgeIds(TenantId tenantId, EntityId entityId) {
        if (!edgesEnabled) {
            return null;
        }
        if (EntityType.EDGE.equals(entityId.getEntityType())) {
            return Collections.singletonList(new EdgeId(entityId.getId()));
        }
        PageDataIterableByTenantIdEntityId<EdgeId> relatedEdgeIdsIterator =
                new PageDataIterableByTenantIdEntityId<>(edgeService::findRelatedEdgeIdsByEntityId, tenantId, entityId, DEFAULT_PAGE_SIZE);
        List<EdgeId> result = new ArrayList<>();
        for (EdgeId edgeId : relatedEdgeIdsIterator) {
            result.add(edgeId);
        }
        return result;
    }

    protected <I extends EntityId> I emptyId(EntityType entityType) {
        return (I) EntityIdFactory.getByTypeAndUuid(entityType, ModelConstants.NULL_UUID);
    }

    protected ListenableFuture<UUID> autoCommit(User user, EntityId entityId) throws Exception {
        if (vcService != null) {
            return vcService.autoCommit(user, entityId);
        } else {
            // We do not support auto-commit for rule engine
            return Futures.immediateFailedFuture(new RuntimeException("Operation not supported!"));
        }
    }

    protected ListenableFuture<UUID> autoCommit(User user, EntityType entityType, List<UUID> entityIds) throws Exception {
        if (vcService != null) {
            return vcService.autoCommit(user, entityType, entityIds);
        } else {
            // We do not support auto-commit for rule engine
            return Futures.immediateFailedFuture(new RuntimeException("Operation not supported!"));
        }
    }
}
