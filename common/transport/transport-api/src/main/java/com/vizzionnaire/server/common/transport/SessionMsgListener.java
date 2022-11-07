package com.vizzionnaire.server.common.transport;

import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.gen.transport.TransportProtos.AttributeUpdateNotificationMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.GetAttributeResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.SessionCloseNotificationProto;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToDeviceRpcRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToServerRpcResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToTransportUpdateCredentialsProto;
import com.vizzionnaire.server.gen.transport.TransportProtos.UplinkNotificationMsg;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by ashvayka on 04.10.18.
 */
public interface SessionMsgListener {

    void onGetAttributesResponse(GetAttributeResponseMsg getAttributesResponse);

    void onAttributeUpdate(UUID sessionId, AttributeUpdateNotificationMsg attributeUpdateNotification);

    void onRemoteSessionCloseCommand(UUID sessionId, SessionCloseNotificationProto sessionCloseNotification);

    void onToDeviceRpcRequest(UUID sessionId, ToDeviceRpcRequestMsg toDeviceRequest);

    void onToServerRpcResponse(ToServerRpcResponseMsg toServerResponse);

    void onDeviceDeleted(DeviceId deviceId);

    default void onUplinkNotification(UplinkNotificationMsg notificationMsg){};

    default void onToTransportUpdateCredentials(ToTransportUpdateCredentialsProto toTransportUpdateCredentials){}

    default void onDeviceProfileUpdate(TransportProtos.SessionInfoProto newSessionInfo, DeviceProfile deviceProfile) {}

    default void onDeviceUpdate(TransportProtos.SessionInfoProto sessionInfo, Device device,
                                Optional<DeviceProfile> deviceProfileOpt) {}

    default void onResourceUpdate(TransportProtos.ResourceUpdateMsg resourceUpdateMsgOpt) {}

    default void onResourceDelete(TransportProtos.ResourceDeleteMsg resourceUpdateMsgOpt) {}
}
