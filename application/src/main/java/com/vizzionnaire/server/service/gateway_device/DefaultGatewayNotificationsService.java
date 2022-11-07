package com.vizzionnaire.server.service.gateway_device;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.DataConstants;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.rpc.ToDeviceRpcRequestBody;
import com.vizzionnaire.server.common.msg.rpc.ToDeviceRpcRequest;
import com.vizzionnaire.server.service.rpc.TbCoreDeviceRpcService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DefaultGatewayNotificationsService implements GatewayNotificationsService {

    private final static String DEVICE_RENAMED_METHOD_NAME = "gateway_device_renamed";
    private final static String DEVICE_DELETED_METHOD_NAME = "gateway_device_deleted";
    private final static Long rpcTimeout = TimeUnit.DAYS.toMillis(1);
    @Lazy
    @Autowired
    private TbCoreDeviceRpcService deviceRpcService;

    @Override
    public void onDeviceUpdated(Device device, Device oldDevice) {
        Optional<DeviceId> gatewayDeviceId = getGatewayDeviceIdFromAdditionalInfoInDevice(device);
        if (gatewayDeviceId.isPresent()) {
            ObjectNode renamedDeviceNode = JacksonUtil.newObjectNode();
            renamedDeviceNode.put(oldDevice.getName(), device.getName());
            ToDeviceRpcRequest rpcRequest = formDeviceToGatewayRPCRequest(device.getTenantId(), gatewayDeviceId.get(), renamedDeviceNode, DEVICE_RENAMED_METHOD_NAME);
            deviceRpcService.processRestApiRpcRequest(rpcRequest, fromDeviceRpcResponse -> {
                log.trace("Device renamed RPC with id: [{}] processed to gateway device with id: [{}], old device name: [{}], new device name: [{}]",
                        rpcRequest.getId(), gatewayDeviceId, oldDevice.getName(), device.getName());
            }, null);
        }
    }

    @Override
    public void onDeviceDeleted(Device device) {
        Optional<DeviceId> gatewayDeviceId = getGatewayDeviceIdFromAdditionalInfoInDevice(device);
        if (gatewayDeviceId.isPresent()) {
            TextNode deletedDeviceNode = new TextNode(device.getName());
            ToDeviceRpcRequest rpcRequest = formDeviceToGatewayRPCRequest(device.getTenantId(), gatewayDeviceId.get(), deletedDeviceNode, DEVICE_DELETED_METHOD_NAME);
            deviceRpcService.processRestApiRpcRequest(rpcRequest, fromDeviceRpcResponse -> {
                log.trace("Device deleted RPC with id: [{}] processed to gateway device with id: [{}], deleted device name: [{}]",
                        rpcRequest.getId(), gatewayDeviceId, device.getName());
            }, null);
        }
    }

    private ToDeviceRpcRequest formDeviceToGatewayRPCRequest(TenantId tenantId, DeviceId gatewayDeviceId, JsonNode deviceDataNode, String method) {
        ToDeviceRpcRequestBody body = new ToDeviceRpcRequestBody(method, JacksonUtil.toString(deviceDataNode));
        long expTime = System.currentTimeMillis() + rpcTimeout;
        UUID rpcRequestUUID = UUID.randomUUID();
        return new ToDeviceRpcRequest(rpcRequestUUID,
                tenantId,
                gatewayDeviceId,
                true,
                expTime,
                body,
                true,
                3,
                null
        );
    }

    private Optional<DeviceId> getGatewayDeviceIdFromAdditionalInfoInDevice(Device device) {
        JsonNode deviceAdditionalInfo = device.getAdditionalInfo();
        if (deviceAdditionalInfo != null && deviceAdditionalInfo.has(DataConstants.LAST_CONNECTED_GATEWAY)) {
            try {
                JsonNode lastConnectedGatewayIdNode = deviceAdditionalInfo.get(DataConstants.LAST_CONNECTED_GATEWAY);
                return Optional.of(new DeviceId(UUID.fromString(lastConnectedGatewayIdNode.asText())));
            } catch (RuntimeException e) {
                log.debug("[{}] Failed to decode connected gateway: {}", device.getId(), deviceAdditionalInfo);
            }
        }
        return Optional.empty();
    }
}