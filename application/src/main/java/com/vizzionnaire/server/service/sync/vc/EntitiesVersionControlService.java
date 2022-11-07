package com.vizzionnaire.server.service.sync.vc;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.exception.ThingsboardException;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.sync.vc.BranchInfo;
import com.vizzionnaire.server.common.data.sync.vc.EntityDataDiff;
import com.vizzionnaire.server.common.data.sync.vc.EntityDataInfo;
import com.vizzionnaire.server.common.data.sync.vc.EntityVersion;
import com.vizzionnaire.server.common.data.sync.vc.RepositorySettings;
import com.vizzionnaire.server.common.data.sync.vc.VersionCreationResult;
import com.vizzionnaire.server.common.data.sync.vc.VersionLoadResult;
import com.vizzionnaire.server.common.data.sync.vc.VersionedEntityInfo;
import com.vizzionnaire.server.common.data.sync.vc.request.create.VersionCreateRequest;
import com.vizzionnaire.server.common.data.sync.vc.request.load.VersionLoadRequest;

import java.util.List;
import java.util.UUID;

public interface EntitiesVersionControlService {

    ListenableFuture<UUID> saveEntitiesVersion(User user, VersionCreateRequest request) throws Exception;

    VersionCreationResult getVersionCreateStatus(User user, UUID requestId) throws ThingsboardException;

    ListenableFuture<PageData<EntityVersion>> listEntityVersions(TenantId tenantId, String branch, EntityId externalId, PageLink pageLink) throws Exception;

    ListenableFuture<PageData<EntityVersion>> listEntityTypeVersions(TenantId tenantId, String branch, EntityType entityType, PageLink pageLink) throws Exception;

    ListenableFuture<PageData<EntityVersion>> listVersions(TenantId tenantId, String branch, PageLink pageLink) throws Exception;

    ListenableFuture<List<VersionedEntityInfo>> listEntitiesAtVersion(TenantId tenantId, String versionId, EntityType entityType) throws Exception;

    ListenableFuture<List<VersionedEntityInfo>> listAllEntitiesAtVersion(TenantId tenantId, String versionId) throws Exception;

    UUID loadEntitiesVersion(User user, VersionLoadRequest request) throws Exception;

    VersionLoadResult getVersionLoadStatus(User user, UUID requestId) throws ThingsboardException;

    ListenableFuture<EntityDataDiff> compareEntityDataToVersion(User user, EntityId entityId, String versionId) throws Exception;

    ListenableFuture<List<BranchInfo>> listBranches(TenantId tenantId) throws Exception;

    RepositorySettings getVersionControlSettings(TenantId tenantId);

    ListenableFuture<RepositorySettings> saveVersionControlSettings(TenantId tenantId, RepositorySettings versionControlSettings);

    ListenableFuture<Void> deleteVersionControlSettings(TenantId tenantId) throws Exception;

    ListenableFuture<Void> checkVersionControlAccess(TenantId tenantId, RepositorySettings settings) throws Exception;

    ListenableFuture<UUID> autoCommit(User user, EntityId entityId) throws Exception;

    ListenableFuture<UUID> autoCommit(User user, EntityType entityType, List<UUID> entityIds) throws Exception;

    ListenableFuture<EntityDataInfo> getEntityDataInfo(User user, EntityId entityId, String versionId);

}
