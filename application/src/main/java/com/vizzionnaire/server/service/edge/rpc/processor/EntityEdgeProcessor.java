package com.vizzionnaire.server.service.edge.rpc.processor;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainConnectionInfo;
import com.vizzionnaire.server.gen.edge.v1.DeviceCredentialsRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.DeviceUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@TbCoreComponent
public class EntityEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg processEntityMergeRequestMessageToEdge(Edge edge, EdgeEvent edgeEvent) {
        DownlinkMsg downlinkMsg = null;
        if (EdgeEventType.DEVICE.equals(edgeEvent.getType())) {
            DeviceId deviceId = new DeviceId(edgeEvent.getEntityId());
            Device device = deviceService.findDeviceById(edge.getTenantId(), deviceId);
            CustomerId customerId = getCustomerIdIfEdgeAssignedToCustomer(device, edge);
            String conflictName = null;
            if(edgeEvent.getBody() != null) {
                conflictName = edgeEvent.getBody().get("conflictName").asText();
            }
            DeviceUpdateMsg deviceUpdateMsg = deviceMsgConstructor
                    .constructDeviceUpdatedMsg(UpdateMsgType.ENTITY_MERGE_RPC_MESSAGE, device, customerId, conflictName);
            downlinkMsg = DownlinkMsg.newBuilder()
                    .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                    .addDeviceUpdateMsg(deviceUpdateMsg)
                    .build();
        }
        return downlinkMsg;
    }

    public DownlinkMsg processCredentialsRequestMessageToEdge(EdgeEvent edgeEvent) {
        DownlinkMsg downlinkMsg = null;
        if (EdgeEventType.DEVICE.equals(edgeEvent.getType())) {
            DeviceId deviceId = new DeviceId(edgeEvent.getEntityId());
            DeviceCredentialsRequestMsg deviceCredentialsRequestMsg = DeviceCredentialsRequestMsg.newBuilder()
                    .setDeviceIdMSB(deviceId.getId().getMostSignificantBits())
                    .setDeviceIdLSB(deviceId.getId().getLeastSignificantBits())
                    .build();
            DownlinkMsg.Builder builder = DownlinkMsg.newBuilder()
                    .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                    .addDeviceCredentialsRequestMsg(deviceCredentialsRequestMsg);
            downlinkMsg = builder.build();
        }
        return downlinkMsg;
    }

    public ListenableFuture<Void> processEntityNotification(TenantId tenantId, TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg) {
        EdgeEventActionType actionType = EdgeEventActionType.valueOf(edgeNotificationMsg.getAction());
        EdgeEventType type = EdgeEventType.valueOf(edgeNotificationMsg.getType());
        EntityId entityId = EntityIdFactory.getByEdgeEventTypeAndUuid(type,
                new UUID(edgeNotificationMsg.getEntityIdMSB(), edgeNotificationMsg.getEntityIdLSB()));
        EdgeId edgeId = safeGetEdgeId(edgeNotificationMsg);
        switch (actionType) {
            case ADDED: // used only for USER entity
            case UPDATED:
            case CREDENTIALS_UPDATED:
                return pushNotificationToAllRelatedEdges(tenantId, entityId, type, actionType);
            case ASSIGNED_TO_CUSTOMER:
            case UNASSIGNED_FROM_CUSTOMER:
                return pushNotificationToAllRelatedCustomerEdges(tenantId, edgeNotificationMsg, entityId, actionType, type);
            case DELETED:
                if (edgeId != null) {
                    return saveEdgeEvent(tenantId, edgeId, type, actionType, entityId, null);
                } else {
                    return pushNotificationToAllRelatedEdges(tenantId, entityId, type, actionType);
                }
            case ASSIGNED_TO_EDGE:
            case UNASSIGNED_FROM_EDGE:
                ListenableFuture<Void> future = saveEdgeEvent(tenantId, edgeId, type, actionType, entityId, null);
                return Futures.transformAsync(future, unused -> {
                    if (type.equals(EdgeEventType.RULE_CHAIN)) {
                        return updateDependentRuleChains(tenantId, new RuleChainId(entityId.getId()), edgeId);
                    } else {
                        return Futures.immediateFuture(null);
                    }
                }, dbCallbackExecutorService);
            default:
                return Futures.immediateFuture(null);
        }
    }

    private ListenableFuture<Void> pushNotificationToAllRelatedCustomerEdges(TenantId tenantId,
                                                                             TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg,
                                                                             EntityId entityId,
                                                                             EdgeEventActionType actionType,
                                                                             EdgeEventType type) {
        PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
        PageData<EdgeId> pageData;
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        do {
            pageData = edgeService.findRelatedEdgeIdsByEntityId(tenantId, entityId, pageLink);
            if (pageData != null && pageData.getData() != null && !pageData.getData().isEmpty()) {
                for (EdgeId relatedEdgeId : pageData.getData()) {
                    try {
                        CustomerId customerId = mapper.readValue(edgeNotificationMsg.getBody(), CustomerId.class);
                        ListenableFuture<Edge> future = edgeService.findEdgeByIdAsync(tenantId, relatedEdgeId);
                        futures.add(Futures.transformAsync(future, edge -> {
                            if (edge != null && edge.getCustomerId() != null &&
                                    !edge.getCustomerId().isNullUid() && edge.getCustomerId().equals(customerId)) {
                                return saveEdgeEvent(tenantId, relatedEdgeId, type, actionType, entityId, null);
                            } else {
                                return Futures.immediateFuture(null);
                            }
                        }, dbCallbackExecutorService));
                    } catch (Exception e) {
                        log.error("Can't parse customer id from entity body [{}]", edgeNotificationMsg, e);
                        return Futures.immediateFailedFuture(e);
                    }
                }
                if (pageData.hasNext()) {
                    pageLink = pageLink.nextPageLink();
                }
            }
        } while (pageData != null && pageData.hasNext());
        return Futures.transform(Futures.allAsList(futures), voids -> null, dbCallbackExecutorService);
    }

    private EdgeId safeGetEdgeId(TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg) {
        if (edgeNotificationMsg.getEdgeIdMSB() != 0 && edgeNotificationMsg.getEdgeIdLSB() != 0) {
            return new EdgeId(new UUID(edgeNotificationMsg.getEdgeIdMSB(), edgeNotificationMsg.getEdgeIdLSB()));
        } else {
            return null;
        }
    }

    private ListenableFuture<Void> pushNotificationToAllRelatedEdges(TenantId tenantId, EntityId entityId, EdgeEventType type, EdgeEventActionType actionType) {
        PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
        PageData<EdgeId> pageData;
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        do {
            pageData = edgeService.findRelatedEdgeIdsByEntityId(tenantId, entityId, pageLink);
            if (pageData != null && pageData.getData() != null && !pageData.getData().isEmpty()) {
                for (EdgeId relatedEdgeId : pageData.getData()) {
                    futures.add(saveEdgeEvent(tenantId, relatedEdgeId, type, actionType, entityId, null));
                }
                if (pageData.hasNext()) {
                    pageLink = pageLink.nextPageLink();
                }
            }
        } while (pageData != null && pageData.hasNext());
        return Futures.transform(Futures.allAsList(futures), voids -> null, dbCallbackExecutorService);
    }

    private ListenableFuture<Void> updateDependentRuleChains(TenantId tenantId, RuleChainId processingRuleChainId, EdgeId edgeId) {
        PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
        PageData<RuleChain> pageData;
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        do {
            pageData = ruleChainService.findRuleChainsByTenantIdAndEdgeId(tenantId, edgeId, pageLink);
            if (pageData != null && pageData.getData() != null && !pageData.getData().isEmpty()) {
                for (RuleChain ruleChain : pageData.getData()) {
                    if (!ruleChain.getId().equals(processingRuleChainId)) {
                        List<RuleChainConnectionInfo> connectionInfos =
                                ruleChainService.loadRuleChainMetaData(ruleChain.getTenantId(), ruleChain.getId()).getRuleChainConnections();
                        if (connectionInfos != null && !connectionInfos.isEmpty()) {
                            for (RuleChainConnectionInfo connectionInfo : connectionInfos) {
                                if (connectionInfo.getTargetRuleChainId().equals(processingRuleChainId)) {
                                    futures.add(saveEdgeEvent(tenantId,
                                            edgeId,
                                            EdgeEventType.RULE_CHAIN_METADATA,
                                            EdgeEventActionType.UPDATED,
                                            ruleChain.getId(),
                                            null));
                                }
                            }
                        }
                    }
                }
                if (pageData.hasNext()) {
                    pageLink = pageLink.nextPageLink();
                }
            }
        } while (pageData != null && pageData.hasNext());
        return Futures.transform(Futures.allAsList(futures), voids -> null, dbCallbackExecutorService);
    }

    public ListenableFuture<Void> processEntityNotificationForAllEdges(TenantId tenantId, TransportProtos.EdgeNotificationMsgProto edgeNotificationMsg) {
        EdgeEventActionType actionType = EdgeEventActionType.valueOf(edgeNotificationMsg.getAction());
        EdgeEventType type = EdgeEventType.valueOf(edgeNotificationMsg.getType());
        EntityId entityId = EntityIdFactory.getByEdgeEventTypeAndUuid(type, new UUID(edgeNotificationMsg.getEntityIdMSB(), edgeNotificationMsg.getEntityIdLSB()));
        switch (actionType) {
            case ADDED:
            case UPDATED:
            case DELETED:
                return processActionForAllEdges(tenantId, type, actionType, entityId);
            default:
                return Futures.immediateFuture(null);
        }
    }
}

