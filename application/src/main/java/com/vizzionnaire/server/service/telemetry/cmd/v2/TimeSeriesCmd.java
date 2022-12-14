package com.vizzionnaire.server.service.telemetry.cmd.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vizzionnaire.server.common.data.kv.Aggregation;

import lombok.Data;

import java.util.List;

@Data
public class TimeSeriesCmd implements GetTsCmd {

    private List<String> keys;
    private long startTs;
    private long timeWindow;
    private long interval;
    private int limit;
    private Aggregation agg;
    private boolean fetchLatestPreviousPoint;

    @JsonIgnore
    @Override
    public long getEndTs() {
        return startTs + timeWindow;
    }
}
