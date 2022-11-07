package com.vizzionnaire.server.service.edge.rpc.constructor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;
import com.vizzionnaire.server.gen.edge.v1.DeviceCredentialsUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.DeviceRpcCallMsg;
import com.vizzionnaire.server.gen.edge.v1.DeviceUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.RpcRequestMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@TbCoreComponent
public class DeviceMsgConstructor {

    protected static final ObjectMapper mapper = new ObjectMapper();

    public DeviceUpdateMsg constructDeviceUpdatedMsg(UpdateMsgType msgType, Device device, CustomerId customerId, String conflictName) {
        DeviceUpdateMsg.Builder builder = DeviceUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(device.getId().getId().getMostSignificantBits())
                .setIdLSB(device.getId().getId().getLeastSignificantBits())
                .setName(device.getName())
                .setType(device.getType());
        if (device.getLabel() != null) {
            builder.setLabel(device.getLabel());
        }
        if (customerId != null) {
            builder.setCustomerIdMSB(customerId.getId().getMostSignificantBits());
            builder.setCustomerIdLSB(customerId.getId().getLeastSignificantBits());
        }
        if (device.getDeviceProfileId() != null) {
            builder.setDeviceProfileIdMSB(device.getDeviceProfileId().getId().getMostSignificantBits());
            builder.setDeviceProfileIdLSB(device.getDeviceProfileId().getId().getLeastSignificantBits());
        }
        if (device.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(device.getAdditionalInfo()));
        }
        if (device.getFirmwareId() != null) {
            builder.setFirmwareIdMSB(device.getFirmwareId().getId().getMostSignificantBits())
                    .setFirmwareIdLSB(device.getFirmwareId().getId().getLeastSignificantBits());
        }
        if (conflictName != null) {
            builder.setConflictName(conflictName);
        }
        return builder.build();
    }

    public DeviceCredentialsUpdateMsg constructDeviceCredentialsUpdatedMsg(DeviceCredentials deviceCredentials) {
        DeviceCredentialsUpdateMsg.Builder builder = DeviceCredentialsUpdateMsg.newBuilder()
                .setDeviceIdMSB(deviceCredentials.getDeviceId().getId().getMostSignificantBits())
                .setDeviceIdLSB(deviceCredentials.getDeviceId().getId().getLeastSignificantBits());
        if (deviceCredentials.getCredentialsType() != null) {
            builder.setCredentialsType(deviceCredentials.getCredentialsType().name())
                    .setCredentialsId(deviceCredentials.getCredentialsId());
        }
        if (deviceCredentials.getCredentialsValue() != null) {
            builder.setCredentialsValue(deviceCredentials.getCredentialsValue());
        }
        return builder.build();
    }

    public DeviceUpdateMsg constructDeviceDeleteMsg(DeviceId deviceId) {
        return DeviceUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(deviceId.getId().getMostSignificantBits())
                .setIdLSB(deviceId.getId().getLeastSignificantBits()).build();
    }

    public DeviceRpcCallMsg constructDeviceRpcCallMsg(UUID deviceId, JsonNode body) {
        int requestId = body.get("requestId").asInt();
        boolean oneway = body.get("oneway").asBoolean();
        UUID requestUUID = UUID.fromString(body.get("requestUUID").asText());
        long expirationTime = body.get("expirationTime").asLong();
        String method = body.get("method").asText();
        String params = body.get("params").asText();

        RpcRequestMsg.Builder requestBuilder = RpcRequestMsg.newBuilder();
        requestBuilder.setMethod(method);
        requestBuilder.setParams(params);
        DeviceRpcCallMsg.Builder builder = DeviceRpcCallMsg.newBuilder()
                .setDeviceIdMSB(deviceId.getMostSignificantBits())
                .setDeviceIdLSB(deviceId.getLeastSignificantBits())
                .setRequestUuidMSB(requestUUID.getMostSignificantBits())
                .setRequestUuidLSB(requestUUID.getLeastSignificantBits())
                .setRequestId(requestId)
                .setExpirationTime(expirationTime)
                .setOneway(oneway)
                .setRequestMsg(requestBuilder.build());
        return builder.build();
    }
}
