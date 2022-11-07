package com.vizzionnaire.server.service.edge.rpc.sync;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.gen.edge.v1.AttributesRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.DeviceCredentialsRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.DeviceProfileDevicesRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.EntityViewsRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.RelationRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.RuleChainMetadataRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.UserCredentialsRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.WidgetBundleTypesRequestMsg;

public interface EdgeRequestsService {

    ListenableFuture<Void> processRuleChainMetadataRequestMsg(TenantId tenantId, Edge edge, RuleChainMetadataRequestMsg ruleChainMetadataRequestMsg);

    ListenableFuture<Void> processAttributesRequestMsg(TenantId tenantId, Edge edge, AttributesRequestMsg attributesRequestMsg);

    ListenableFuture<Void> processRelationRequestMsg(TenantId tenantId, Edge edge, RelationRequestMsg relationRequestMsg);

    ListenableFuture<Void> processDeviceCredentialsRequestMsg(TenantId tenantId, Edge edge, DeviceCredentialsRequestMsg deviceCredentialsRequestMsg);

    ListenableFuture<Void> processUserCredentialsRequestMsg(TenantId tenantId, Edge edge, UserCredentialsRequestMsg userCredentialsRequestMsg);

    ListenableFuture<Void> processDeviceProfileDevicesRequestMsg(TenantId tenantId, Edge edge, DeviceProfileDevicesRequestMsg deviceProfileDevicesRequestMsg);

    ListenableFuture<Void> processWidgetBundleTypesRequestMsg(TenantId tenantId, Edge edge, WidgetBundleTypesRequestMsg widgetBundleTypesRequestMsg);

    ListenableFuture<Void> processEntityViewsRequestMsg(TenantId tenantId, Edge edge, EntityViewsRequestMsg entityViewsRequestMsg);
}
