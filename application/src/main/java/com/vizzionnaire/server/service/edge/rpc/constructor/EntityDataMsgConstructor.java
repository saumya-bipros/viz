package com.vizzionnaire.server.service.edge.rpc.constructor;

import com.google.gson.reflect.TypeToken;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.transport.adaptor.JsonConverter;
import com.vizzionnaire.server.gen.edge.v1.AttributeDeleteMsg;
import com.vizzionnaire.server.gen.edge.v1.EntityDataProto;
import com.vizzionnaire.server.gen.transport.TransportProtos;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@TbCoreComponent
public class EntityDataMsgConstructor {

    public EntityDataProto constructEntityDataMsg(EntityId entityId, EdgeEventActionType actionType, JsonElement entityData) {
        EntityDataProto.Builder builder = EntityDataProto.newBuilder()
                .setEntityIdMSB(entityId.getId().getMostSignificantBits())
                .setEntityIdLSB(entityId.getId().getLeastSignificantBits())
                .setEntityType(entityId.getEntityType().name());
        switch (actionType) {
            case TIMESERIES_UPDATED:
                try {
                    JsonObject data = entityData.getAsJsonObject();
                    long ts;
                    if (data.get("ts") != null && !data.get("ts").isJsonNull()) {
                        ts = data.getAsJsonPrimitive("ts").getAsLong();
                    } else {
                        ts = System.currentTimeMillis();
                    }
                    builder.setPostTelemetryMsg(JsonConverter.convertToTelemetryProto(data.getAsJsonObject("data"), ts));
                } catch (Exception e) {
                    log.warn("[{}] Can't convert to telemetry proto, entityData [{}]", entityId, entityData, e);
                }
                break;
            case ATTRIBUTES_UPDATED:
                try {
                    JsonObject data = entityData.getAsJsonObject();
                    TransportProtos.PostAttributeMsg attributesUpdatedMsg = JsonConverter.convertToAttributesProto(data.getAsJsonObject("kv"));
                    builder.setAttributesUpdatedMsg(attributesUpdatedMsg);
                    builder.setPostAttributeScope(data.getAsJsonPrimitive("scope").getAsString());
                } catch (Exception e) {
                    log.warn("[{}] Can't convert to AttributesUpdatedMsg proto, entityData [{}]", entityId, entityData, e);
                }
                break;
            case POST_ATTRIBUTES:
                try {
                    JsonObject data = entityData.getAsJsonObject();
                    TransportProtos.PostAttributeMsg postAttributesMsg = JsonConverter.convertToAttributesProto(data.getAsJsonObject("kv"));
                    builder.setPostAttributesMsg(postAttributesMsg);
                    builder.setPostAttributeScope(data.getAsJsonPrimitive("scope").getAsString());
                } catch (Exception e) {
                    log.warn("[{}] Can't convert to PostAttributesMsg, entityData [{}]", entityId, entityData, e);
                }
                break;
            case ATTRIBUTES_DELETED:
                try {
                    AttributeDeleteMsg.Builder attributeDeleteMsg = AttributeDeleteMsg.newBuilder();
                    attributeDeleteMsg.setScope(entityData.getAsJsonObject().getAsJsonPrimitive("scope").getAsString());
                    JsonArray jsonArray = entityData.getAsJsonObject().getAsJsonArray("keys");
                    List<String> keys = new Gson().fromJson(jsonArray.toString(), new TypeToken<>(){}.getType());
                    attributeDeleteMsg.addAllAttributeNames(keys);
                    attributeDeleteMsg.build();
                    builder.setAttributeDeleteMsg(attributeDeleteMsg);
                } catch (Exception e) {
                    log.warn("[{}] Can't convert to AttributeDeleteMsg proto, entityData [{}]", entityId, entityData, e);
                }
                break;
        }
        return builder.build();
    }

}
