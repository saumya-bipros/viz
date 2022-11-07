package com.vizzionnaire.rule.engine.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.data.RelationsQuery;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.common.data.relation.EntityRelationsQuery;
import com.vizzionnaire.server.common.data.relation.EntitySearchDirection;
import com.vizzionnaire.server.common.data.relation.RelationsSearchParameters;
import com.vizzionnaire.server.dao.relation.RelationService;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class EntitiesRelatedEntityIdAsyncLoader {

    public static ListenableFuture<EntityId> findEntityAsync(TbContext ctx, EntityId originator,
                                                             RelationsQuery relationsQuery) {
        RelationService relationService = ctx.getRelationService();
        EntityRelationsQuery query = buildQuery(originator, relationsQuery);
        ListenableFuture<List<EntityRelation>> asyncRelation = relationService.findByQuery(ctx.getTenantId(), query);
        if (relationsQuery.getDirection() == EntitySearchDirection.FROM) {
            return Futures.transformAsync(asyncRelation, r -> CollectionUtils.isNotEmpty(r) ? Futures.immediateFuture(r.get(0).getTo())
                    : Futures.immediateFuture(null), MoreExecutors.directExecutor());
        } else if (relationsQuery.getDirection() == EntitySearchDirection.TO) {
            return Futures.transformAsync(asyncRelation, r -> CollectionUtils.isNotEmpty(r) ? Futures.immediateFuture(r.get(0).getFrom())
                    : Futures.immediateFuture(null), MoreExecutors.directExecutor());
        }
        return Futures.immediateFailedFuture(new IllegalStateException("Unknown direction"));
    }

    private static EntityRelationsQuery buildQuery(EntityId originator, RelationsQuery relationsQuery) {
        EntityRelationsQuery query = new EntityRelationsQuery();
        RelationsSearchParameters parameters = new RelationsSearchParameters(originator,
                relationsQuery.getDirection(), relationsQuery.getMaxLevel(), relationsQuery.isFetchLastLevelOnly());
        query.setParameters(parameters);
        query.setFilters(relationsQuery.getFilters());
        return query;
    }
}
