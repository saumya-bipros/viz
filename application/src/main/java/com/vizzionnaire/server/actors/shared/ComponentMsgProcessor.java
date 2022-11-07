package com.vizzionnaire.server.actors.shared;

import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActorCtx;
import com.vizzionnaire.server.actors.stats.StatsPersistTick;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleState;
import com.vizzionnaire.server.common.data.tenant.profile.TenantProfileConfiguration;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.queue.PartitionChangeMsg;
import com.vizzionnaire.server.common.msg.queue.RuleNodeException;

@Slf4j
public abstract class ComponentMsgProcessor<T extends EntityId> extends AbstractContextAwareMsgProcessor {

    protected final TenantId tenantId;
    protected final T entityId;
    protected ComponentLifecycleState state;

    protected ComponentMsgProcessor(ActorSystemContext systemContext, TenantId tenantId, T id) {
        super(systemContext);
        this.tenantId = tenantId;
        this.entityId = id;
    }

    protected TenantProfileConfiguration getTenantProfileConfiguration() {
        return systemContext.getTenantProfileCache().get(tenantId).getProfileData().getConfiguration();
    }

    public abstract String getComponentName();

    public abstract void start(TbActorCtx context) throws Exception;

    public abstract void stop(TbActorCtx context) throws Exception;

    public abstract void onPartitionChangeMsg(PartitionChangeMsg msg) throws Exception;

    public void onCreated(TbActorCtx context) throws Exception {
        start(context);
    }

    public void onUpdate(TbActorCtx context) throws Exception {
        restart(context);
    }

    public void onActivate(TbActorCtx context) throws Exception {
        restart(context);
    }

    public void onSuspend(TbActorCtx context) throws Exception {
        stop(context);
    }

    public void onStop(TbActorCtx context) throws Exception {
        stop(context);
    }

    private void restart(TbActorCtx context) throws Exception {
        stop(context);
        start(context);
    }

    public void scheduleStatsPersistTick(TbActorCtx context, long statsPersistFrequency) {
        schedulePeriodicMsgWithDelay(context, new StatsPersistTick(), statsPersistFrequency, statsPersistFrequency);
    }

    protected boolean checkMsgValid(TbMsg tbMsg) {
        var valid = tbMsg.isValid();
        if (!valid) {
            if (log.isTraceEnabled()) {
                log.trace("Skip processing of message: {} because it is no longer valid!", tbMsg);
            }
        }
        return valid;
    }

    protected void checkComponentStateActive(TbMsg tbMsg) throws RuleNodeException {
        if (state != ComponentLifecycleState.ACTIVE) {
            log.debug("Component is not active. Current state [{}] for processor [{}][{}] tenant [{}]", state, entityId.getEntityType(), entityId, tenantId);
            RuleNodeException ruleNodeException = getInactiveException();
            if (tbMsg != null) {
                tbMsg.getCallback().onFailure(ruleNodeException);
            }
            throw ruleNodeException;
        }
    }

    abstract protected RuleNodeException getInactiveException();

}
