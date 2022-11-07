package com.vizzionnaire.server.service.edge.rpc.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.gen.edge.v1.DeviceProfileUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@Slf4j
@TbCoreComponent
public class DeviceProfileEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg processDeviceProfileToEdge(EdgeEvent edgeEvent, UpdateMsgType msgType, EdgeEventActionType action) {
        DeviceProfileId deviceProfileId = new DeviceProfileId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (action) {
            case ADDED:
            case UPDATED:
                DeviceProfile deviceProfile = deviceProfileService.findDeviceProfileById(edgeEvent.getTenantId(), deviceProfileId);
                if (deviceProfile != null) {
                    DeviceProfileUpdateMsg deviceProfileUpdateMsg =
                            deviceProfileMsgConstructor.constructDeviceProfileUpdatedMsg(msgType, deviceProfile);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addDeviceProfileUpdateMsg(deviceProfileUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
                DeviceProfileUpdateMsg deviceProfileUpdateMsg =
                        deviceProfileMsgConstructor.constructDeviceProfileDeleteMsg(deviceProfileId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addDeviceProfileUpdateMsg(deviceProfileUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }

}
