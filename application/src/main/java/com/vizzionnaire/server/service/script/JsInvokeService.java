package com.vizzionnaire.server.service.script;

import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.TenantId;

import java.util.UUID;

public interface JsInvokeService {

    ListenableFuture<UUID> eval(TenantId tenantId, JsScriptType scriptType, String scriptBody, String... argNames);

    ListenableFuture<Object> invokeFunction(TenantId tenantId, CustomerId customerId, UUID scriptId, Object... args);

    ListenableFuture<Void> release(UUID scriptId);

}
