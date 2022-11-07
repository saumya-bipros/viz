package com.vizzionnaire.server.common.data.kv;

public interface DeleteTsKvQuery extends TsKvQuery {

    Boolean getRewriteLatestIfDeleted();

}
