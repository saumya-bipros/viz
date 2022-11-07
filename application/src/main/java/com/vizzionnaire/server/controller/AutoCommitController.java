package com.vizzionnaire.server.controller;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.service.sync.vc.EntitiesVersionControlService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class AutoCommitController extends BaseController {

    @Autowired
    private EntitiesVersionControlService vcService;

    protected ListenableFuture<UUID> autoCommit(User user, EntityId entityId) throws Exception {
        if (vcService != null) {
            return vcService.autoCommit(user, entityId);
        } else {
            // We do not support auto-commit for rule engine
            return Futures.immediateFailedFuture(new RuntimeException("Operation not supported!"));
        }
    }


}
