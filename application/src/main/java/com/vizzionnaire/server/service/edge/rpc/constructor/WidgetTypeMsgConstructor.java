package com.vizzionnaire.server.service.edge.rpc.constructor;

import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.WidgetTypeId;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.gen.edge.v1.WidgetTypeUpdateMsg;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class WidgetTypeMsgConstructor {

    public WidgetTypeUpdateMsg constructWidgetTypeUpdateMsg(UpdateMsgType msgType, WidgetTypeDetails widgetTypeDetails) {
        WidgetTypeUpdateMsg.Builder builder = WidgetTypeUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setIdMSB(widgetTypeDetails.getId().getId().getMostSignificantBits())
                .setIdLSB(widgetTypeDetails.getId().getId().getLeastSignificantBits());
        if (widgetTypeDetails.getBundleAlias() != null) {
            builder.setBundleAlias(widgetTypeDetails.getBundleAlias());
        }
        if (widgetTypeDetails.getAlias() != null) {
            builder.setAlias(widgetTypeDetails.getAlias());
        }
        if (widgetTypeDetails.getName() != null) {
            builder.setName(widgetTypeDetails.getName());
        }
        if (widgetTypeDetails.getDescriptor() != null) {
            builder.setDescriptorJson(JacksonUtil.toString(widgetTypeDetails.getDescriptor()));
        }
        if (widgetTypeDetails.getTenantId().equals(TenantId.SYS_TENANT_ID)) {
            builder.setIsSystem(true);
        }
        if (widgetTypeDetails.getImage() != null) {
            builder.setImage(widgetTypeDetails.getImage());
        }
        if (widgetTypeDetails.getDescription() != null) {
            builder.setDescription(widgetTypeDetails.getDescription());
        }
        return builder.build();
    }

    public WidgetTypeUpdateMsg constructWidgetTypeDeleteMsg(WidgetTypeId widgetTypeId) {
        return WidgetTypeUpdateMsg.newBuilder()
                .setMsgType(UpdateMsgType.ENTITY_DELETED_RPC_MESSAGE)
                .setIdMSB(widgetTypeId.getId().getMostSignificantBits())
                .setIdLSB(widgetTypeId.getId().getLeastSignificantBits())
                .build();
    }
}
