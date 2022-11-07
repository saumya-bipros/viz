package com.vizzionnaire.server.service.telemetry.cmd.v2;

import lombok.Getter;

import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.service.telemetry.sub.SubscriptionErrorCode;

import java.util.List;

public abstract class DataUpdate<T> extends CmdUpdate {

    @Getter
    private final PageData<T> data;
    @Getter
    private final List<T> update;

    public DataUpdate(int cmdId, PageData<T> data, List<T> update, int errorCode, String errorMsg) {
        super(cmdId, errorCode, errorMsg);
        this.data = data;
        this.update = update;
    }

    public DataUpdate(int cmdId, PageData<T> data, List<T> update) {
        this(cmdId, data, update, SubscriptionErrorCode.NO_ERROR.getCode(), null);
    }

    public DataUpdate(int cmdId, int errorCode, String errorMsg) {
        this(cmdId, null, null, errorCode, errorMsg);
    }

}