package com.vizzionnaire.server.common.msg.aware;

import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.msg.TbActorMsg;

public interface DeviceAwareMsg extends TbActorMsg {

    DeviceId getDeviceId();
}
