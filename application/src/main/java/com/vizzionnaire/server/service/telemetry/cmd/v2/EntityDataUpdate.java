package com.vizzionnaire.server.service.telemetry.cmd.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.query.EntityData;
import com.vizzionnaire.server.service.telemetry.sub.SubscriptionErrorCode;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
public class EntityDataUpdate extends DataUpdate<EntityData> {

    @Getter
    private long allowedEntities;

    public EntityDataUpdate(int cmdId, PageData<EntityData> data, List<EntityData> update, long allowedEntities) {
        super(cmdId, data, update, SubscriptionErrorCode.NO_ERROR.getCode(), null);
        this.allowedEntities = allowedEntities;
    }

    public EntityDataUpdate(int cmdId, int errorCode, String errorMsg) {
        super(cmdId, null, null, errorCode, errorMsg);
    }

    @Override
    public CmdUpdateType getCmdUpdateType() {
        return CmdUpdateType.ENTITY_DATA;
    }

    @JsonCreator
    public EntityDataUpdate(@JsonProperty("cmdId") int cmdId,
                            @JsonProperty("data") PageData<EntityData> data,
                            @JsonProperty("update") List<EntityData> update,
                            @JsonProperty("errorCode") int errorCode,
                            @JsonProperty("errorMsg") String errorMsg) {
        super(cmdId, data, update, errorCode, errorMsg);
    }

}
