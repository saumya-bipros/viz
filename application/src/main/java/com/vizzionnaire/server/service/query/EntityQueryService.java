package com.vizzionnaire.server.service.query;

import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.query.AlarmData;
import com.vizzionnaire.server.common.data.query.AlarmDataQuery;
import com.vizzionnaire.server.common.data.query.EntityCountQuery;
import com.vizzionnaire.server.common.data.query.EntityData;
import com.vizzionnaire.server.common.data.query.EntityDataQuery;
import com.vizzionnaire.server.service.security.model.SecurityUser;

public interface EntityQueryService {

    long countEntitiesByQuery(SecurityUser securityUser, EntityCountQuery query);

    PageData<EntityData> findEntityDataByQuery(SecurityUser securityUser, EntityDataQuery query);

    PageData<AlarmData> findAlarmDataByQuery(SecurityUser securityUser, AlarmDataQuery query);

    DeferredResult<ResponseEntity> getKeysByQuery(SecurityUser securityUser, TenantId tenantId, EntityDataQuery query,
                                                  boolean isTimeseries, boolean isAttributes);

}
