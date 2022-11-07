package com.vizzionnaire.server.actors.ruleChain;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.vizzionnaire.server.actors.ActorSystemContext;
import com.vizzionnaire.server.actors.TbActorRef;
import com.vizzionnaire.server.actors.TbEntityActorId;
import com.vizzionnaire.server.actors.TbEntityTypeActorIdPredicate;
import com.vizzionnaire.server.actors.service.ContextAwareActor;
import com.vizzionnaire.server.actors.service.DefaultActorService;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageDataIterable;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainType;
import com.vizzionnaire.server.common.msg.TbActorMsg;
import com.vizzionnaire.server.dao.rule.RuleChainService;

import java.util.function.Function;

/**
 * Created by ashvayka on 15.03.18.
 */
@Slf4j
public abstract class RuleChainManagerActor extends ContextAwareActor {

    protected final TenantId tenantId;
    private final RuleChainService ruleChainService;
    @Getter
    protected RuleChain rootChain;
    @Getter
    protected TbActorRef rootChainActor;

    public RuleChainManagerActor(ActorSystemContext systemContext, TenantId tenantId) {
        super(systemContext);
        this.tenantId = tenantId;
        this.ruleChainService = systemContext.getRuleChainService();
    }

    protected void initRuleChains() {
        for (RuleChain ruleChain : new PageDataIterable<>(link -> ruleChainService.findTenantRuleChainsByType(tenantId, RuleChainType.CORE, link), ContextAwareActor.ENTITY_PACK_LIMIT)) {
            RuleChainId ruleChainId = ruleChain.getId();
            log.debug("[{}|{}] Creating rule chain actor", ruleChainId.getEntityType(), ruleChain.getId());
            TbActorRef actorRef = getOrCreateActor(ruleChainId, id -> ruleChain);
            visit(ruleChain, actorRef);
            log.debug("[{}|{}] Rule Chain actor created.", ruleChainId.getEntityType(), ruleChainId.getId());
        }
    }

    protected void destroyRuleChains() {
        for (RuleChain ruleChain : new PageDataIterable<>(link -> ruleChainService.findTenantRuleChainsByType(tenantId, RuleChainType.CORE, link), ContextAwareActor.ENTITY_PACK_LIMIT)) {
            ctx.stop(new TbEntityActorId(ruleChain.getId()));
        }
    }

    protected void visit(RuleChain entity, TbActorRef actorRef) {
        if (entity != null && entity.isRoot() && entity.getType().equals(RuleChainType.CORE)) {
            rootChain = entity;
            rootChainActor = actorRef;
        }
    }

    protected TbActorRef getOrCreateActor(RuleChainId ruleChainId) {
        return getOrCreateActor(ruleChainId, eId -> ruleChainService.findRuleChainById(TenantId.SYS_TENANT_ID, eId));
    }

    protected TbActorRef getOrCreateActor(RuleChainId ruleChainId, Function<RuleChainId, RuleChain> provider) {
        return ctx.getOrCreateChildActor(new TbEntityActorId(ruleChainId),
                () -> DefaultActorService.RULE_DISPATCHER_NAME,
                () -> {
                    RuleChain ruleChain = provider.apply(ruleChainId);
                    return new RuleChainActor.ActorCreator(systemContext, tenantId, ruleChain);
                });
    }

    protected TbActorRef getEntityActorRef(EntityId entityId) {
        TbActorRef target = null;
        if (entityId.getEntityType() == EntityType.RULE_CHAIN) {
            target = getOrCreateActor((RuleChainId) entityId);
        }
        return target;
    }

    protected void broadcast(TbActorMsg msg) {
        ctx.broadcastToChildren(msg, new TbEntityTypeActorIdPredicate(EntityType.RULE_CHAIN));
    }
}
