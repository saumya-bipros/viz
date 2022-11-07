package com.vizzionnaire.rule.engine.util;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.rule.engine.api.TbContext;
import com.vizzionnaire.rule.engine.api.TbNodeException;
import com.vizzionnaire.server.common.data.BaseData;
import com.vizzionnaire.server.common.data.EntityFieldsData;
import com.vizzionnaire.server.common.data.id.AlarmId;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;

import java.util.function.Function;

public class EntitiesFieldsAsyncLoader {

    public static ListenableFuture<EntityFieldsData> findAsync(TbContext ctx, EntityId original) {
        switch (original.getEntityType()) {
            case TENANT:
                return getAsync(ctx.getTenantService().findTenantByIdAsync(ctx.getTenantId(), (TenantId) original),
                        EntityFieldsData::new);
            case CUSTOMER:
                return getAsync(ctx.getCustomerService().findCustomerByIdAsync(ctx.getTenantId(), (CustomerId) original),
                        EntityFieldsData::new);
            case USER:
                return getAsync(ctx.getUserService().findUserByIdAsync(ctx.getTenantId(), (UserId) original),
                        EntityFieldsData::new);
            case ASSET:
                return getAsync(ctx.getAssetService().findAssetByIdAsync(ctx.getTenantId(), (AssetId) original),
                        EntityFieldsData::new);
            case DEVICE:
                return getAsync(ctx.getDeviceService().findDeviceByIdAsync(ctx.getTenantId(), (DeviceId) original),
                        EntityFieldsData::new);
            case ALARM:
                return getAsync(ctx.getAlarmService().findAlarmByIdAsync(ctx.getTenantId(), (AlarmId) original),
                        EntityFieldsData::new);
            case RULE_CHAIN:
                return getAsync(ctx.getRuleChainService().findRuleChainByIdAsync(ctx.getTenantId(), (RuleChainId) original),
                        EntityFieldsData::new);
            case ENTITY_VIEW:
                return getAsync(ctx.getEntityViewService().findEntityViewByIdAsync(ctx.getTenantId(), (EntityViewId) original),
                        EntityFieldsData::new);
            default:
                return Futures.immediateFailedFuture(new TbNodeException("Unexpected original EntityType " + original.getEntityType()));
        }
    }

    private static <T extends BaseData> ListenableFuture<EntityFieldsData> getAsync(
            ListenableFuture<T> future, Function<T, EntityFieldsData> converter) {
        return Futures.transformAsync(future, in -> in != null ?
                Futures.immediateFuture(converter.apply(in))
                : Futures.immediateFailedFuture(new RuntimeException("Entity not found!")), MoreExecutors.directExecutor());
    }
}
