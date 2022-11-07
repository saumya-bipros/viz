package com.vizzionnaire.server.service.edge.rpc.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.AdminSettings;
import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.gen.edge.v1.AdminSettingsUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@Slf4j
@TbCoreComponent
public class AdminSettingsEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg processAdminSettingsToEdge(EdgeEvent edgeEvent) {
        AdminSettings adminSettings = mapper.convertValue(edgeEvent.getBody(), AdminSettings.class);
        AdminSettingsUpdateMsg adminSettingsUpdateMsg = adminSettingsMsgConstructor.constructAdminSettingsUpdateMsg(adminSettings);
        return DownlinkMsg.newBuilder()
                .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                .addAdminSettingsUpdateMsg(adminSettingsUpdateMsg)
                .build();
    }

}
