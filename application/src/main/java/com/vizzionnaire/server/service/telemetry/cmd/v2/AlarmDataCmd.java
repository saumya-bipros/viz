package com.vizzionnaire.server.service.telemetry.cmd.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vizzionnaire.server.common.data.query.AlarmDataQuery;

import lombok.Getter;

public class AlarmDataCmd extends DataCmd {

    @Getter
    private final AlarmDataQuery query;

    @JsonCreator
    public AlarmDataCmd(@JsonProperty("cmdId") int cmdId, @JsonProperty("query") AlarmDataQuery query) {
        super(cmdId);
        this.query = query;
    }
}
