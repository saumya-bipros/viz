package com.vizzionnaire.server.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

import static com.vizzionnaire.server.controller.ControllerConstants.DEVICE_ID_PARAM_DESCRIPTION;
import static com.vizzionnaire.server.controller.ControllerConstants.TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH;

import java.util.UUID;

@RestController
@TbCoreComponent
@RequestMapping(TbUrlConstants.RPC_V1_URL_PREFIX)
@Slf4j
public class RpcV1Controller extends AbstractRpcController {

    @ApiOperation(value = "Send one-way RPC request (handleOneWayDeviceRPCRequest)", notes = "Deprecated. See 'Rpc V 2 Controller' instead." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/oneway/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> handleOneWayDeviceRPCRequest(
            @ApiParam(value = DEVICE_ID_PARAM_DESCRIPTION)
            @PathVariable("deviceId") String deviceIdStr,
            @ApiParam(value = "A JSON value representing the RPC request.")
            @RequestBody String requestBody) throws VizzionnaireException {
        return handleDeviceRPCRequest(true, new DeviceId(UUID.fromString(deviceIdStr)), requestBody, HttpStatus.REQUEST_TIMEOUT, HttpStatus.CONFLICT);
    }

    @ApiOperation(value = "Send two-way RPC request (handleTwoWayDeviceRPCRequest)", notes = "Deprecated. See 'Rpc V 2 Controller' instead." + TENANT_OR_CUSTOMER_AUTHORITY_PARAGRAPH)
    @PreAuthorize("hasAnyAuthority('SYS_ADMIN', 'TENANT_ADMIN', 'CUSTOMER_USER')")
    @RequestMapping(value = "/twoway/{deviceId}", method = RequestMethod.POST)
    @ResponseBody
    public DeferredResult<ResponseEntity> handleTwoWayDeviceRPCRequest(
            @ApiParam(value = DEVICE_ID_PARAM_DESCRIPTION)
            @PathVariable("deviceId") String deviceIdStr,
            @ApiParam(value = "A JSON value representing the RPC request.")
            @RequestBody String requestBody) throws VizzionnaireException {
        return handleDeviceRPCRequest(false, new DeviceId(UUID.fromString(deviceIdStr)), requestBody, HttpStatus.REQUEST_TIMEOUT, HttpStatus.CONFLICT);
    }

}
