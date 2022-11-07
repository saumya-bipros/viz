package com.vizzionnaire.server.service.edge.rpc.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.id.WidgetsBundleId;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.gen.edge.v1.WidgetsBundleUpdateMsg;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@Slf4j
@TbCoreComponent
public class WidgetBundleEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg processWidgetsBundleToEdge(EdgeEvent edgeEvent, UpdateMsgType msgType, EdgeEventActionType edgeEdgeEventActionType) {
        WidgetsBundleId widgetsBundleId = new WidgetsBundleId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEdgeEventActionType) {
            case ADDED:
            case UPDATED:
                WidgetsBundle widgetsBundle = widgetsBundleService.findWidgetsBundleById(edgeEvent.getTenantId(), widgetsBundleId);
                if (widgetsBundle != null) {
                    WidgetsBundleUpdateMsg widgetsBundleUpdateMsg =
                            widgetsBundleMsgConstructor.constructWidgetsBundleUpdateMsg(msgType, widgetsBundle);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addWidgetsBundleUpdateMsg(widgetsBundleUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
                WidgetsBundleUpdateMsg widgetsBundleUpdateMsg =
                        widgetsBundleMsgConstructor.constructWidgetsBundleDeleteMsg(widgetsBundleId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addWidgetsBundleUpdateMsg(widgetsBundleUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }
}
