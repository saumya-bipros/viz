package com.vizzionnaire.server.common.transport.session;

import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.gen.transport.TransportProtos;

import java.util.Optional;
import java.util.UUID;

public interface SessionContext {

    UUID getSessionId();

    int nextMsgId();

    void onDeviceProfileUpdate(TransportProtos.SessionInfoProto sessionInfo, DeviceProfile deviceProfile);

    void onDeviceUpdate(TransportProtos.SessionInfoProto sessionInfo, Device device, Optional<DeviceProfile> deviceProfileOpt);
}
