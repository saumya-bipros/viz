package com.vizzionnaire.server.service.edge.rpc.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.vizzionnaire.server.common.data.EdgeUtils;
import com.vizzionnaire.server.common.data.edge.EdgeEvent;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.id.QueueId;
import com.vizzionnaire.server.common.data.queue.Queue;
import com.vizzionnaire.server.gen.edge.v1.DownlinkMsg;
import com.vizzionnaire.server.gen.edge.v1.QueueUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@Slf4j
@TbCoreComponent
public class QueueEdgeProcessor extends BaseEdgeProcessor {

    public DownlinkMsg processQueueToEdge(EdgeEvent edgeEvent, UpdateMsgType msgType, EdgeEventActionType action) {
        QueueId queueId = new QueueId(edgeEvent.getEntityId());
        DownlinkMsg downlinkMsg = null;
        switch (action) {
            case ADDED:
            case UPDATED:
                Queue queue = queueService.findQueueById(edgeEvent.getTenantId(), queueId);
                if (queue != null) {
                    QueueUpdateMsg queueUpdateMsg =
                            queueMsgConstructor.constructQueueUpdatedMsg(msgType, queue);
                    downlinkMsg = DownlinkMsg.newBuilder()
                            .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                            .addQueueUpdateMsg(queueUpdateMsg)
                            .build();
                }
                break;
            case DELETED:
                QueueUpdateMsg queueDeleteMsg =
                        queueMsgConstructor.constructQueueDeleteMsg(queueId);
                downlinkMsg = DownlinkMsg.newBuilder()
                        .setDownlinkMsgId(EdgeUtils.nextPositiveInt())
                        .addQueueUpdateMsg(queueDeleteMsg)
                        .build();
                break;
        }
        return downlinkMsg;
    }

}
