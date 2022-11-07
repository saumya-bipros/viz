package com.vizzionnaire.server.service.entitiy.asset;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.TenantId;

public interface TbAssetService {

    Asset save(Asset asset, User user) throws Exception;

    ListenableFuture<Void> delete(Asset asset, User user);

    Asset assignAssetToCustomer(TenantId tenantId, AssetId assetId, Customer customer, User user) throws ThingsboardException;

    Asset unassignAssetToCustomer(TenantId tenantId, AssetId assetId, Customer customer, User user) throws ThingsboardException;

    Asset assignAssetToPublicCustomer(TenantId tenantId, AssetId assetId, User user) throws ThingsboardException;

    Asset assignAssetToEdge(TenantId tenantId, AssetId assetId, Edge edge, User user) throws ThingsboardException;

    Asset unassignAssetFromEdge(TenantId tenantId, Asset asset, Edge edge, User user) throws ThingsboardException;

}
