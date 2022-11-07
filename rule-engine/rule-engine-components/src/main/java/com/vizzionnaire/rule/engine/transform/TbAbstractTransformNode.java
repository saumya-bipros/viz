package com.vizzionnaire.rule.engine.transform;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNode;
import com.vizzionnaire.rule.engine.api.TbNodeConfiguration;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.rule.engine.api.util.TbNodeUtils;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.queue.RuleEngineException;
import com.vizzionnaire.server.common.msg.queue.TbMsgCallback;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.vizzionnaire.common.util.DonAsynchron.withCallback;
import static com.vizzionnaire.rule.engine.api.TbRelationTypes.FAILURE;

/**
 * Created by ashvayka on 19.01.18.
 */
@Slf4j
public abstract class TbAbstractTransformNode implements TbNode {

    private TbTransformNodeConfiguration config;

    @Override
    public void init(TbContext context, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbTransformNodeConfiguration.class);
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        withCallback(transform(ctx, msg),
                m -> transformSuccess(ctx, msg, m),
                t -> transformFailure(ctx, msg, t),
                MoreExecutors.directExecutor());
    }

    protected void transformFailure(TbContext ctx, TbMsg msg, Throwable t) {
        ctx.tellFailure(msg, t);
    }

    protected void transformSuccess(TbContext ctx, TbMsg msg, TbMsg m) {
        if (m != null) {
            ctx.tellSuccess(m);
        } else {
            ctx.tellNext(msg, FAILURE);
        }
    }

    protected void transformSuccess(TbContext ctx, TbMsg msg, List<TbMsg> msgs) {
        if (msgs != null && !msgs.isEmpty()) {
            if (msgs.size() == 1) {
                ctx.tellSuccess(msgs.get(0));
            } else {
                TbMsgCallbackWrapper wrapper = new MultipleTbMsgsCallbackWrapper(msgs.size(), new TbMsgCallback() {
                    @Override
                    public void onSuccess() {
                        ctx.ack(msg);
                    }

                    @Override
                    public void onFailure(RuleEngineException e) {
                        ctx.tellFailure(msg, e);
                    }
                });
                msgs.forEach(newMsg -> ctx.enqueueForTellNext(newMsg, "Success", wrapper::onSuccess, wrapper::onFailure));
            }
        } else {
            ctx.tellNext(msg, FAILURE);
        }
    }

    protected abstract ListenableFuture<List<TbMsg>> transform(TbContext ctx, TbMsg msg);

    public void setConfig(TbTransformNodeConfiguration config) {
        this.config = config;
    }
}
