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
public class EntityCountUpdate extends CmdUpdate {

    @Getter
    private int count;

    public EntityCountUpdate(int cmdId, int count) {
        super(cmdId, SubscriptionErrorCode.NO_ERROR.getCode(), null);
        this.count = count;
    }

    public EntityCountUpdate(int cmdId, int errorCode, String errorMsg) {
        super(cmdId, errorCode, errorMsg);
    }

    @Override
    public CmdUpdateType getCmdUpdateType() {
        return CmdUpdateType.COUNT_DATA;
    }

    @JsonCreator
    public EntityCountUpdate(@JsonProperty("cmdId") int cmdId,
                             @JsonProperty("count") int count,
                             @JsonProperty("errorCode") int errorCode,
                             @JsonProperty("errorMsg") String errorMsg) {
        super(cmdId, errorCode, errorMsg);
        this.count = count;
    }

}
