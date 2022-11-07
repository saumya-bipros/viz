package com.vizzionnaire.server.service.edge.rpc.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.HasCustomerId;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.dao.alarm.AlarmService;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.attributes.AttributesService;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.dashboard.DashboardService;
import com.vizzionnaire.server.dao.device.DeviceCredentialsService;
import com.vizzionnaire.server.dao.device.DeviceProfileService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.queue.QueueService;
import com.vizzionnaire.server.dao.relation.RelationService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.dao.widget.WidgetTypeService;
import com.vizzionnaire.server.dao.widget.WidgetsBundleService;
import com.vizzionnaire.server.queue.discovery.PartitionService;
import com.vizzionnaire.server.queue.provider.TbQueueProducerProvider;
import com.vizzionnaire.server.service.edge.rpc.constructor.AdminSettingsMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.AlarmMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.AssetMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.CustomerMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.DashboardMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.DeviceMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.DeviceProfileMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.EntityDataMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.EntityViewMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.OtaPackageMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.QueueMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.RelationMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.RuleChainMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.UserMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.WidgetTypeMsgConstructor;
import com.vizzionnaire.server.service.edge.rpc.constructor.WidgetsBundleMsgConstructor;
import com.vizzionnaire.server.service.executors.DbCallbackExecutorService;
import com.vizzionnaire.server.service.profile.TbDeviceProfileCache;
import com.vizzionnaire.server.service.state.DeviceStateService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseEdgeProcessor {

    protected static final ObjectMapper mapper = new ObjectMapper();

    protected static final int DEFAULT_PAGE_SIZE = 1000;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected AlarmService alarmService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected TbDeviceProfileCache deviceProfileCache;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected AssetService assetService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired
    protected TenantService tenantService;

    @Autowired
    protected EdgeService edgeService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    protected RelationService relationService;

    @Autowired
    protected DeviceCredentialsService deviceCredentialsService;

    @Autowired
    protected AttributesService attributesService;

    @Autowired
    protected TbClusterService tbClusterService;

    @Autowired
    protected DeviceStateService deviceStateService;

    @Autowired
    protected EdgeEventService edgeEventService;

    @Autowired
    protected WidgetsBundleService widgetsBundleService;

    @Autowired
    protected WidgetTypeService widgetTypeService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected QueueService queueService;

    @Autowired
    protected PartitionService partitionService;

    @Autowired
    @Lazy
    protected TbQueueProducerProvider producerProvider;

    @Autowired
    protected DataValidator<Device> deviceValidator;

    @Autowired
    protected EntityDataMsgConstructor entityDataMsgConstructor;

    @Autowired
    protected RuleChainMsgConstructor ruleChainMsgConstructor;

    @Autowired
    protected AlarmMsgConstructor alarmMsgConstructor;

    @Autowired
    protected DeviceMsgConstructor deviceMsgConstructor;

    @Autowired
    protected AssetMsgConstructor assetMsgConstructor;

    @Autowired
    protected EntityViewMsgConstructor entityViewMsgConstructor;

    @Autowired
    protected DashboardMsgConstructor dashboardMsgConstructor;

    @Autowired
    protected RelationMsgConstructor relationMsgConstructor;

    @Autowired
    protected UserMsgConstructor userMsgConstructor;

    @Autowired
    protected CustomerMsgConstructor customerMsgConstructor;

    @Autowired
    protected DeviceProfileMsgConstructor deviceProfileMsgConstructor;

    @Autowired
    protected WidgetsBundleMsgConstructor widgetsBundleMsgConstructor;

    @Autowired
    protected WidgetTypeMsgConstructor widgetTypeMsgConstructor;

    @Autowired
    protected AdminSettingsMsgConstructor adminSettingsMsgConstructor;

    @Autowired
    protected OtaPackageMsgConstructor otaPackageMsgConstructor;

    @Autowired
    protected QueueMsgConstructor queueMsgConstructor;

    @Autowired
    protected DbCallbackExecutorService dbCallbackExecutorService;

    protected ListenableFuture<Void> saveEdgeEvent(TenantId tenantId,
                                                     EdgeId edgeId,
                                                     EdgeEventType type,
                                                     EdgeEventActionType action,
                                                     EntityId entityId,
                                                     JsonNode body) {
        log.debug("Pushing event to edge queue. tenantId [{}], edgeId [{}], type[{}], " +
                        "action [{}], entityId [{}], body [{}]",
                tenantId, edgeId, type, action, entityId, body);

        EdgeEvent edgeEvent = EdgeUtils.constructEdgeEvent(tenantId, edgeId, type, action, entityId, body);

        return Futures.transform(edgeEventService.saveAsync(edgeEvent), unused -> {
            tbClusterService.onEdgeEventUpdate(tenantId, edgeId);
            return null;
        }, dbCallbackExecutorService);
    }

    protected CustomerId getCustomerIdIfEdgeAssignedToCustomer(HasCustomerId hasCustomerIdEntity, Edge edge) {
        if (!edge.getCustomerId().isNullUid() && edge.getCustomerId().equals(hasCustomerIdEntity.getCustomerId())) {
            return edge.getCustomerId();
        } else {
            return null;
        }
    }

    protected ListenableFuture<Void> processActionForAllEdges(TenantId tenantId, EdgeEventType type, EdgeEventActionType actionType, EntityId entityId) {
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        if (TenantId.SYS_TENANT_ID.equals(tenantId)) {
            PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
            PageData<TenantId> tenantsIds;
            do {
                tenantsIds = tenantService.findTenantsIds(pageLink);
                for (TenantId tenantId1 : tenantsIds.getData()) {
                    futures.addAll(processActionForAllEdgesByTenantId(tenantId1, type, actionType, entityId));
                }
                pageLink = pageLink.nextPageLink();
            } while (tenantsIds.hasNext());
        } else {
            futures = processActionForAllEdgesByTenantId(tenantId, type, actionType, entityId);
        }
        return Futures.transform(Futures.allAsList(futures), voids -> null, dbCallbackExecutorService);
    }

    private List<ListenableFuture<Void>> processActionForAllEdgesByTenantId(TenantId tenantId, EdgeEventType type, EdgeEventActionType actionType, EntityId entityId) {
        PageLink pageLink = new PageLink(DEFAULT_PAGE_SIZE);
        PageData<Edge> pageData;
        List<ListenableFuture<Void>> futures = new ArrayList<>();
        do {
            pageData = edgeService.findEdgesByTenantId(tenantId, pageLink);
            if (pageData != null && pageData.getData() != null && !pageData.getData().isEmpty()) {
                for (Edge edge : pageData.getData()) {
                    futures.add(saveEdgeEvent(tenantId, edge.getId(), type, actionType, entityId, null));
                }
                if (pageData.hasNext()) {
                    pageLink = pageLink.nextPageLink();
                }
            }
        } while (pageData != null && pageData.hasNext());
        return futures;
    }
}
