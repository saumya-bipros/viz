package com.vizzionnaire.server.actors.device;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.rule.engine.api.msg.DeviceAttributesEventNotificationMsg;
import com.vizzionnaire.rule.engine.api.msg.DeviceEdgeUpdateMsg;
import com.vizzionnaire.rule.engine.api.msg.DeviceNameOrTypeUpdateMsg;
import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActorCtx;
import com.vizzionnaire.server.actors.TbActorException;
import com.vizzionnaire.server.actors.service.ContextAwareActor;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.TbActorMsg;
import com.vizzionnaire.server.common.msg.timeout.DeviceActorServerSideRpcTimeoutMsg;
import com.vizzionnaire.server.service.rpc.FromDeviceRpcResponseActorMsg;
import com.vizzionnaire.server.service.rpc.RemoveRpcActorMsg;
import com.vizzionnaire.server.service.rpc.ToDeviceRpcRequestActorMsg;
import com.vizzionnaire.server.service.transport.msg.TransportToDeviceActorMsgWrapper;

@Slf4j
public class DeviceActor extends ContextAwareActor {

    private final DeviceActorMessageProcessor processor;

    DeviceActor(ActorSystemContext systemContext, TenantId tenantId, DeviceId deviceId) {
        super(systemContext);
        this.processor = new DeviceActorMessageProcessor(systemContext, tenantId, deviceId);
    }

    @Override
    public void init(TbActorCtx ctx) throws TbActorException {
        super.init(ctx);
        log.debug("[{}][{}] Starting device actor.", processor.tenantId, processor.deviceId);
        try {
            processor.init(ctx);
            log.debug("[{}][{}] Device actor started.", processor.tenantId, processor.deviceId);
        } catch (Exception e) {
            log.warn("[{}][{}] Unknown failure", processor.tenantId, processor.deviceId, e);
            throw new TbActorException("Failed to initialize device actor", e);
        }
    }

    @Override
    protected boolean doProcess(TbActorMsg msg) {
        switch (msg.getMsgType()) {
            case TRANSPORT_TO_DEVICE_ACTOR_MSG:
                processor.process(ctx, (TransportToDeviceActorMsgWrapper) msg);
                break;
            case DEVICE_ATTRIBUTES_UPDATE_TO_DEVICE_ACTOR_MSG:
                processor.processAttributesUpdate(ctx, (DeviceAttributesEventNotificationMsg) msg);
                break;
            case DEVICE_CREDENTIALS_UPDATE_TO_DEVICE_ACTOR_MSG:
                processor.processCredentialsUpdate(msg);
                break;
            case DEVICE_NAME_OR_TYPE_UPDATE_TO_DEVICE_ACTOR_MSG:
                processor.processNameOrTypeUpdate((DeviceNameOrTypeUpdateMsg) msg);
                break;
            case DEVICE_RPC_REQUEST_TO_DEVICE_ACTOR_MSG:
                processor.processRpcRequest(ctx, (ToDeviceRpcRequestActorMsg) msg);
                break;
            case DEVICE_RPC_RESPONSE_TO_DEVICE_ACTOR_MSG:
                processor.processRpcResponsesFromEdge(ctx, (FromDeviceRpcResponseActorMsg) msg);
                break;
            case DEVICE_ACTOR_SERVER_SIDE_RPC_TIMEOUT_MSG:
                processor.processServerSideRpcTimeout(ctx, (DeviceActorServerSideRpcTimeoutMsg) msg);
                break;
            case SESSION_TIMEOUT_MSG:
                processor.checkSessionsTimeout();
                break;
            case DEVICE_EDGE_UPDATE_TO_DEVICE_ACTOR_MSG:
                processor.processEdgeUpdate((DeviceEdgeUpdateMsg) msg);
                break;
            case REMOVE_RPC_TO_DEVICE_ACTOR_MSG:
                processor.processRemoveRpc(ctx, (RemoveRpcActorMsg) msg);
                break;
            default:
                return false;
        }
        return true;
    }

}
