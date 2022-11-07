package com.vizzionnaire.server.service.transport.msg;

import lombok.Data;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.msg.MsgType;
import com.vizzionnaire.server.common.msg.TbActorMsg;
import com.vizzionnaire.server.common.msg.aware.DeviceAwareMsg;
import com.vizzionnaire.server.common.msg.aware.TenantAwareMsg;
import com.vizzionnaire.server.common.msg.queue.TbCallback;
import com.vizzionnaire.server.gen.transport.TransportProtos.TransportToDeviceActorMsg;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by ashvayka on 09.10.18.
 */
@Data
public class TransportToDeviceActorMsgWrapper implements TbActorMsg, DeviceAwareMsg, TenantAwareMsg, Serializable {

    private static final long serialVersionUID = 7191333353202935941L;

    private final TenantId tenantId;
    private final DeviceId deviceId;
    private final TransportToDeviceActorMsg msg;
    private final TbCallback callback;

    public TransportToDeviceActorMsgWrapper(TransportToDeviceActorMsg msg, TbCallback callback) {
        this.msg = msg;
        this.callback = callback;
        this.tenantId = TenantId.fromUUID(new UUID(msg.getSessionInfo().getTenantIdMSB(), msg.getSessionInfo().getTenantIdLSB()));
        this.deviceId = new DeviceId(new UUID(msg.getSessionInfo().getDeviceIdMSB(), msg.getSessionInfo().getDeviceIdLSB()));
    }

    @Override
    public MsgType getMsgType() {
        return MsgType.TRANSPORT_TO_DEVICE_ACTOR_MSG;
    }
}
