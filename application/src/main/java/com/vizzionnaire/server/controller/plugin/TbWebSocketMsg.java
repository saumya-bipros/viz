package com.vizzionnaire.server.controller.plugin;

public interface TbWebSocketMsg<T> {

    TbWebSocketMsgType getType();

    T getMsg();

}
