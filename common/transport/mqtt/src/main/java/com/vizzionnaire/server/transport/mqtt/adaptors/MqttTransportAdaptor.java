package com.vizzionnaire.server.transport.mqtt.adaptors;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.codec.mqtt.MqttFixedHeader;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttPublishVariableHeader;

import com.vizzionnaire.server.common.data.ota.OtaPackageType;
import com.vizzionnaire.server.common.transport.adaptor.AdaptorException;
import com.vizzionnaire.server.gen.transport.TransportProtos.AttributeUpdateNotificationMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ClaimDeviceMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.GetAttributeRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.GetAttributeResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.PostAttributeMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.PostTelemetryMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ProvisionDeviceRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ProvisionDeviceResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToDeviceRpcRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToDeviceRpcResponseMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToServerRpcRequestMsg;
import com.vizzionnaire.server.gen.transport.TransportProtos.ToServerRpcResponseMsg;
import com.vizzionnaire.server.transport.mqtt.session.MqttDeviceAwareSessionContext;

import java.util.Optional;

/**
 * @author Andrew Shvayka
 */
public interface MqttTransportAdaptor {

    ByteBufAllocator ALLOCATOR = new UnpooledByteBufAllocator(false);

    PostTelemetryMsg convertToPostTelemetry(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    PostAttributeMsg convertToPostAttributes(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    GetAttributeRequestMsg convertToGetAttributes(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound, String topicBase) throws AdaptorException;

    ToDeviceRpcResponseMsg convertToDeviceRpcResponse(MqttDeviceAwareSessionContext ctx, MqttPublishMessage mqttMsg, String topicBase) throws AdaptorException;

    ToServerRpcRequestMsg convertToServerRpcRequest(MqttDeviceAwareSessionContext ctx, MqttPublishMessage mqttMsg, String topicBase) throws AdaptorException;

    ClaimDeviceMsg convertToClaimDevice(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, GetAttributeResponseMsg responseMsg, String topicBase) throws AdaptorException;

    Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, GetAttributeResponseMsg responseMsg) throws AdaptorException;

    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, AttributeUpdateNotificationMsg notificationMsg, String topic) throws AdaptorException;

    Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, AttributeUpdateNotificationMsg notificationMsg) throws AdaptorException;

    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, ToDeviceRpcRequestMsg rpcRequest, String topicBase) throws AdaptorException;

    Optional<MqttMessage> convertToGatewayPublish(MqttDeviceAwareSessionContext ctx, String deviceName, ToDeviceRpcRequestMsg rpcRequest) throws AdaptorException;

    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, ToServerRpcResponseMsg rpcResponse, String topicBase) throws AdaptorException;

    ProvisionDeviceRequestMsg convertToProvisionRequestMsg(MqttDeviceAwareSessionContext ctx, MqttPublishMessage inbound) throws AdaptorException;

    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, ProvisionDeviceResponseMsg provisionResponse) throws AdaptorException;

    Optional<MqttMessage> convertToPublish(MqttDeviceAwareSessionContext ctx, byte[] firmwareChunk, String requestId, int chunk, OtaPackageType firmwareType) throws AdaptorException;

    default MqttPublishMessage createMqttPublishMsg(MqttDeviceAwareSessionContext ctx, String topic, byte[] payloadInBytes) {
        MqttFixedHeader mqttFixedHeader =
                new MqttFixedHeader(MqttMessageType.PUBLISH, false, ctx.getQoSForTopic(topic), false, 0);
        MqttPublishVariableHeader header = new MqttPublishVariableHeader(topic, ctx.nextMsgId());
        ByteBuf payload = ALLOCATOR.buffer();
        payload.writeBytes(payloadInBytes);
        return new MqttPublishMessage(mqttFixedHeader, header, payload);
    }
}
