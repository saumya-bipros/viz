package com.vizzionnaire.server.common.data.kv;

public interface TsKvQuery {

    String getKey();

    long getStartTs();

    long getEndTs();

}
