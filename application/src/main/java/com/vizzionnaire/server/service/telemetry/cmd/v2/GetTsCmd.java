package com.vizzionnaire.server.service.telemetry.cmd.v2;

import java.util.List;

import com.vizzionnaire.server.common.data.kv.Aggregation;

public interface GetTsCmd {

    long getStartTs();

    long getEndTs();

    List<String> getKeys();

    long getInterval();

    int getLimit();

    Aggregation getAgg();

    boolean isFetchLatestPreviousPoint();

}
