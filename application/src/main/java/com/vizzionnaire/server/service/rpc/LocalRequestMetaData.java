package com.vizzionnaire.server.service.rpc;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.vizzionnaire.server.common.msg.rpc.ToDeviceRpcRequest;
import com.vizzionnaire.server.service.security.model.SecurityUser;

/**
 * Created by ashvayka on 16.04.18.
 */
@Data
public class LocalRequestMetaData {
    private final ToDeviceRpcRequest request;
    private final SecurityUser user;
    private final DeferredResult<ResponseEntity> responseWriter;
}
