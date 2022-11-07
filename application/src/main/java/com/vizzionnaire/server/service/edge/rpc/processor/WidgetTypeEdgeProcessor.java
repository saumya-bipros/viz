package com.vizzionnaire.server.service.edge.rpc.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.id.WidgetTypeId;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.gen.edge.v1.WidgetTypeUpdateMsg;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@Slf4j
@TbCoreComponent
public class WidgetTypeEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg processWidgetTypeToEdge(EdgeEvent edgeEvent, UpdateMsgType msgType, EdgeEventActionType edgeEdgeEventActionType) {
        WidgetTypeId widgetTypeId = new WidgetTypeId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (edgeEdgeEventActionType) {
            case ADDED:
            case UPDATED:
                WidgetTypeDetails widgetTypeDetails = widgetTypeService.findWidgetTypeDetailsById(edgeEvent.getTenantId(), widgetTypeId);
                if (widgetTypeDetails != null) {
                    WidgetTypeUpdateMsg widgetTypeUpdateMsg =
                            widgetTypeMsgConstructor.constructWidgetTypeUpdateMsg(msgType, widgetTypeDetails);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addWidgetTypeUpdateMsg(widgetTypeUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
                WidgetTypeUpdateMsg widgetTypeUpdateMsg =
                        widgetTypeMsgConstructor.constructWidgetTypeDeleteMsg(widgetTypeId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addWidgetTypeUpdateMsg(widgetTypeUpdateMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }

}
