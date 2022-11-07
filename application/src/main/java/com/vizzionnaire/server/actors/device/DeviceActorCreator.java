package com.vizzionnaire.server.actors.device;

import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActor;
import com.vizzionnaire.server.actors.TbActorId;
import com.vizzionnaire.server.actors.TbEntityActorId;
import com.vizzionnaire.server.actors.service.ContextBasedCreator;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.TenantId;

public class DeviceActorCreator extends ContextBasedCreator {

    private final TenantId tenantId;
    private final DeviceId deviceId;

    public DeviceActorCreator(ActorSystemContext context, TenantId tenantId, DeviceId deviceId) {
        super(context);
        this.tenantId = tenantId;
        this.deviceId = deviceId;
    }

    @Override
    public TbActorId createActorId() {
        return new TbEntityActorId(deviceId);
    }

    @Override
    public TbActor createActor() {
        return new DeviceActor(context, tenantId, deviceId);
    }

}
