package com.vizzionnaire.server.service.telemetry.cmd.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.query.EntityCountQuery;
import com.vizzionnaire.server.common.data.query.EntityDataQuery;

import lombok.Getter;

public class EntityCountCmd extends DataCmd {

    @Getter
    private final EntityCountQuery query;

    @JsonCreator
    public EntityCountCmd(@JsonProperty("cmdId") int cmdId,
                          @JsonProperty("query") EntityCountQuery query) {
        super(cmdId);
        this.query = query;
    }
}
