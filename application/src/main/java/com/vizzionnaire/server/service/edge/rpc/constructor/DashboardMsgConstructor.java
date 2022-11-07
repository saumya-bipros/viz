package com.vizzionnaire.server.service.edge.rpc.constructor;

import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.Dashboard;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DashboardId;
import com.vizzionnaire.server.gen.edge.v1.DashboardUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class DashboardMsgConstructor {

    public DashboardUpdateMsg constructDashboardUpdatedMsg(UpdateMsgType msgType, Dashboard dashboard, CustomerId customerId) {
        DashboardUpdateMsg.Builder builder = DashboardUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(dashboard.getId().getId().getMostSignificantBits())
                .setIdLSB(dashboard.getId().getId().getLeastSignificantBits())
                .setTitle(dashboard.getTitle())
                .setConfiguration(JacksonUtil.toString(dashboard.getConfiguration()));
        if (customerId != null) {
            builder.setCustomerIdMSB(customerId.getId().getMostSignificantBits());
            builder.setCustomerIdLSB(customerId.getId().getLeastSignificantBits());
        }
        return builder.build();
    }

    public DashboardUpdateMsg constructDashboardDeleteMsg(DashboardId dashboardId) {
        return DashboardUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(dashboardId.getId().getMostSignificantBits())
                .setIdLSB(dashboardId.getId().getLeastSignificantBits()).build();
    }

}
