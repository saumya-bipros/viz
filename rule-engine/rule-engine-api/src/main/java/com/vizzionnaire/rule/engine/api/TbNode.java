package com.vizzionnaire.rule.engine.api;

import java.util.concurrent.ExecutionException;

import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.queue.PartitionChangeMsg;

/**
 * Created by ashvayka on 19.01.18.
 */
public interface TbNode {

    void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException;

    void onMsg(TbContext ctx, TbMsg msg) throws ExecutionException, InterruptedException, TbNodeException;

    void destroy();

    default void onPartitionChangeMsg(TbContext ctx, PartitionChangeMsg msg) {}

}
