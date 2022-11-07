package com.vizzionnaire.server.service.edge.rpc.constructor;

import org.springframework.stereotype.Component;

import com.vizzionnaire.common.util.JacksonUtil;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.gen.edge.v1.RelationUpdateMsg;
import com.vizzionnaire.server.gen.edge.v1.UpdateMsgType;
import com.vizzionnaire.server.queue.util.TbCoreComponent;

@Component
@TbCoreComponent
public class RelationMsgConstructor {

    public RelationUpdateMsg constructRelationUpdatedMsg(UpdateMsgType msgType, EntityRelation entityRelation) {
        RelationUpdateMsg.Builder builder = RelationUpdateMsg.newBuilder()
                .setMsgType(msgType)
                .setFromIdMSB(entityRelation.getFrom().getId().getMostSignificantBits())
                .setFromIdLSB(entityRelation.getFrom().getId().getLeastSignificantBits())
                .setFromEntityType(entityRelation.getFrom().getEntityType().name())
                .setToIdMSB(entityRelation.getTo().getId().getMostSignificantBits())
                .setToIdLSB(entityRelation.getTo().getId().getLeastSignificantBits())
                .setToEntityType(entityRelation.getTo().getEntityType().name())
                .setType(entityRelation.getType());
        if (entityRelation.getAdditionalInfo() != null) {
            builder.setAdditionalInfo(JacksonUtil.toString(entityRelation.getAdditionalInfo()));
        }
        if (entityRelation.getTypeGroup() != null) {
            builder.setTypeGroup(entityRelation.getTypeGroup().name());
        }
        return builder.build();
    }
}
