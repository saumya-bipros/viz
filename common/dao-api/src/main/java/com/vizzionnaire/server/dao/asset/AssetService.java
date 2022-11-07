package com.vizzionnaire.server.dao.asset;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.EntitySubtype;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.asset.AssetInfo;
import com.vizzionnaire.server.common.data.asset.AssetSearchQuery;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.page.TimePageLink;

import java.util.List;
import java.util.Optional;

public interface AssetService {

    AssetInfo findAssetInfoById(TenantId tenantId, AssetId assetId);

    Asset findAssetById(TenantId tenantId, AssetId assetId);

    ListenableFuture<Asset> findAssetByIdAsync(TenantId tenantId, AssetId assetId);

    Asset findAssetByTenantIdAndName(TenantId tenantId, String name);

    Asset saveAsset(Asset asset);

    Asset assignAssetToCustomer(TenantId tenantId, AssetId assetId, CustomerId customerId);

    Asset unassignAssetFromCustomer(TenantId tenantId, AssetId assetId);

    void deleteAsset(TenantId tenantId, AssetId assetId);

    PageData<Asset> findAssetsByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<AssetInfo> findAssetInfosByTenantId(TenantId tenantId, PageLink pageLink);

    PageData<Asset> findAssetsByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    PageData<AssetInfo> findAssetInfosByTenantIdAndType(TenantId tenantId, String type, PageLink pageLink);

    ListenableFuture<List<Asset>> findAssetsByTenantIdAndIdsAsync(TenantId tenantId, List<AssetId> assetIds);

    void deleteAssetsByTenantId(TenantId tenantId);

    PageData<Asset> findAssetsByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerId(TenantId tenantId, CustomerId customerId, PageLink pageLink);

    PageData<Asset> findAssetsByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    PageData<AssetInfo> findAssetInfosByTenantIdAndCustomerIdAndType(TenantId tenantId, CustomerId customerId, String type, PageLink pageLink);

    ListenableFuture<List<Asset>> findAssetsByTenantIdCustomerIdAndIdsAsync(TenantId tenantId, CustomerId customerId, List<AssetId> assetIds);

    void unassignCustomerAssets(TenantId tenantId, CustomerId customerId);

    ListenableFuture<List<Asset>> findAssetsByQuery(TenantId tenantId, AssetSearchQuery query);

    ListenableFuture<List<EntitySubtype>> findAssetTypesByTenantId(TenantId tenantId);

    Asset assignAssetToEdge(TenantId tenantId, AssetId assetId, EdgeId edgeId);

    Asset unassignAssetFromEdge(TenantId tenantId, AssetId assetId, EdgeId edgeId);

    PageData<Asset> findAssetsByTenantIdAndEdgeId(TenantId tenantId, EdgeId edgeId, PageLink pageLink);

    PageData<Asset> findAssetsByTenantIdAndEdgeIdAndType(TenantId tenantId, EdgeId edgeId, String type, PageLink pageLink);
}
