package com.vizzionnaire.server.service.edge;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.common.util.VizzionnaireThreadFactory;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.edge.rpc.processor.AlarmEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.CustomerEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.EdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.EntityEdgeProcessor;
import com.vizzionnaire.server.service.edge.rpc.processor.RelationEdgeProcessor;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@TbCoreComponent
@Slf4j
public class DefaultEdgeNotificationService implements EdgeNotificationService {

    @Autowired
    private EdgeService edgeService;

    @Autowired
    private EdgeEventService edgeEventService;

    @Autowired
    private TbClusterService clusterService;

    @Autowired
    private EdgeProcessor edgeProcessor;

    @Autowired
    private EntityEdgeProcessor entityProcessor;

    @Autowired
    private AlarmEdgeProcessor alarmProcessor;

    @Autowired
    private RelationEdgeProcessor relationProcessor;

    @Autowired
    private CustomerEdgeProcessor customerProcessor;

    private ExecutorService dbCallBackExecutor;

    @PostConstruct
    public void initExecutor() {
        dbCallBackExecutor = Executors.newSingleThreadExecutor(VizzionnaireThreadFactory.forName("edge-notifications"));
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (dbCallBackExecutor != null) {
            dbCallBackExecutor.shutdownNow();
        }
    }

    @Override
    public Edge setEdgeRootRuleChain(TenantId tenantId, Edge edge, RuleChainId ruleChainId) throws Exception {
        edge.setRootRuleChainId(ruleChainId);
        Edge savedEdge = edgeService.saveEdge(edge);
        saveEdgeEvent(tenantId, edge.getId(), EdgeEventType.RULE_CHAIN, EdgeEventActionType.UPDATED, ruleChainId, null).get();
        return savedEdge;
    }

    private ListenableFuture<Void> saveEdgeEvent(TenantId tenantId,
                               EdgeId edgeId,
                               EdgeEventType type,
                               EdgeEventActionType action,
                               EntityId entityId,
                               JsonNode body) {
        log.debug("Pushing edge event to edge queue. tenantId [{}], edgeId [{}], type [{}], action[{}], entityId [{}], body [{}]",
                tenantId, edgeId, type, action, entityId, body);

        EdgeEvent edgeEvent = EdgeUtils.constructEdgeEvent(tenantId, edgeId, type, action, entityId, body);

        return Futures.transform(edgeEventService.saveAsync(edgeEvent), unused -> {
            clusterService.onEdgeEventUpdate(tenantId, edgeId);
            return null;
        }, dbCallBackExecutor);
    }

    @Override
    public void pushNotificationToEdge(TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg, TbCallback callback) {
        log.debug("Pushing notification to edge {}", edgeNotificationMsg);
        try {
            TenantId tenantId = TenantId.fromUUID(new UUID(edgeNotificationMsg.getTenantIdMSB(), edgeNotificationMsg.getTenantIdLSB()));
            EdgeEventType type = EdgeEventType.valueOf(edgeNotificationMsg.getType());
            ListenableFuture<Void> future;
            switch (type) {
                case EDGE:
                    future = edgeProcessor.processEdgeNotification(tenantId, edgeNotificationMsg);
                    break;
                case USER:
                case ASSET:
                case DEVICE:
                case DEVICE_PROFILE:
                case ENTITY_VIEW:
                case DASHBOARD:
                case RULE_CHAIN:
                case OTA_PACKAGE:
                    future = entityProcessor.processEntityNotification(tenantId, edgeNotificationMsg);
                    break;
                case CUSTOMER:
                    future = customerProcessor.processCustomerNotification(tenantId, edgeNotificationMsg);
                    break;
                case WIDGETS_BUNDLE:
                case WIDGET_TYPE:
                case QUEUE:
                    future = entityProcessor.processEntityNotificationForAllEdges(tenantId, edgeNotificationMsg);
                    break;
                case ALARM:
                    future = alarmProcessor.processAlarmNotification(tenantId, edgeNotificationMsg);
                    break;
                case RELATION:
                    future = relationProcessor.processRelationNotification(tenantId, edgeNotificationMsg);
                    break;
                default:
                    log.warn("Edge event type [{}] is not designed to be pushed to edge", type);
                    future = Futures.immediateFuture(null);
            }
            Futures.addCallback(future, new FutureCallback<>() {
                @Override
                public void onSuccess(@Nullable Void unused) {
                    callback.onSuccess();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    callBackFailure(edgeNotificationMsg, callback, throwable);
                }
            }, dbCallBackExecutor);
        } catch (Exception e) {
            callBackFailure(edgeNotificationMsg, callback, e);
        }
    }

    private void callBackFailure(TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg, TbCallback callback, Throwable throwable) {
        String errMsg = String.format("Can't push to edge updates, edgeNotificationMsg [%s]", edgeNotificationMsg);
        log.error(errMsg, throwable);
        callback.onFailure(throwable);
    }

}


