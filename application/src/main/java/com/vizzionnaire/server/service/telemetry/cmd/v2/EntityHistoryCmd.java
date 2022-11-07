package com.vizzionnaire.server.service.telemetry.cmd.v2;

import lombok.Data;

import java.util.List;

import com.vizzionnaire.server.common.data.kv.Aggregation;

@Data
public class EntityHistoryCmd implements GetTsCmd {

    private List<String> keys;
    private long startTs;
    private long endTs;
    private long interval;
    private int limit;
    private Aggregation agg;
    private boolean fetchLatestPreviousPoint;

}
