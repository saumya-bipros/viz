package com.vizzionnaire.server.dao.usagerecord;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.vizzionnaire.server.common.data.ApiFeature;
import com.vizzionnaire.server.common.data.ApiUsageRecordKey;
import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.ApiUsageStateValue;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.id.ApiUsageStateId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.kv.BasicTsKvEntry;
import com.vizzionnaire.server.common.data.kv.LongDataEntry;
import com.vizzionnaire.server.common.data.kv.StringDataEntry;
import com.vizzionnaire.server.common.data.kv.TsKvEntry;
import com.vizzionnaire.server.common.data.tenant.profile.TenantProfileConfiguration;
import com.vizzionnaire.server.dao.entity.AbstractEntityService;
import com.vizzionnaire.server.dao.service.DataValidator;
import com.vizzionnaire.server.dao.tenant.TenantProfileDao;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.timeseries.TimeseriesService;
import com.vizzionnaire.server.dao.usagerecord.ApiUsageStateService;

import static com.vizzionnaire.server.dao.service.Validator.validateId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ApiUsageStateServiceImpl extends AbstractEntityService implements ApiUsageStateService {
    public static final String INCORRECT_TENANT_ID = "Incorrect tenantId ";

    private final ApiUsageStateDao apiUsageStateDao;
    private final TenantProfileDao tenantProfileDao;
    private final TenantService tenantService;
    private final TimeseriesService tsService;
    private final DataValidator<ApiUsageState> apiUsageStateValidator;

    public ApiUsageStateServiceImpl(ApiUsageStateDao apiUsageStateDao, TenantProfileDao tenantProfileDao,
                                    TenantService tenantService, @Lazy TimeseriesService tsService,
                                    DataValidator<ApiUsageState> apiUsageStateValidator) {
        this.apiUsageStateDao = apiUsageStateDao;
        this.tenantProfileDao = tenantProfileDao;
        this.tenantService = tenantService;
        this.tsService = tsService;
        this.apiUsageStateValidator = apiUsageStateValidator;
    }

    @Override
    public void deleteApiUsageStateByTenantId(TenantId tenantId) {
        log.trace("Executing deleteUsageRecordsByTenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        apiUsageStateDao.deleteApiUsageStateByTenantId(tenantId);
    }

    @Override
    public void deleteApiUsageStateByEntityId(EntityId entityId) {
        log.trace("Executing deleteApiUsageStateByEntityId [{}]", entityId);
        validateId(entityId.getId(), "Invalid entity id");
        apiUsageStateDao.deleteApiUsageStateByEntityId(entityId);
    }

    @Override
    public ApiUsageState createDefaultApiUsageState(TenantId tenantId, EntityId entityId) {
        entityId = Objects.requireNonNullElse(entityId, tenantId);
        log.trace("Executing createDefaultUsageRecord [{}]", entityId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        ApiUsageState apiUsageState = new ApiUsageState();
        apiUsageState.setTenantId(tenantId);
        apiUsageState.setEntityId(entityId);
        apiUsageState.setTransportState(ApiUsageStateValue.ENABLED);
        apiUsageState.setReExecState(ApiUsageStateValue.ENABLED);
        apiUsageState.setJsExecState(ApiUsageStateValue.ENABLED);
        apiUsageState.setDbStorageState(ApiUsageStateValue.ENABLED);
        apiUsageState.setSmsExecState(ApiUsageStateValue.ENABLED);
        apiUsageState.setEmailExecState(ApiUsageStateValue.ENABLED);
        apiUsageState.setAlarmExecState(ApiUsageStateValue.ENABLED);
        apiUsageStateValidator.validate(apiUsageState, ApiUsageState::getTenantId);

        ApiUsageState saved = apiUsageStateDao.save(apiUsageState.getTenantId(), apiUsageState);

        List<TsKvEntry> apiUsageStates = new ArrayList<>();
        apiUsageStates.add(new BasicTsKvEntry(saved.getCreatedTime(),
                new StringDataEntry(ApiFeature.TRANSPORT.getApiStateKey(), ApiUsageStateValue.ENABLED.name())));
        apiUsageStates.add(new BasicTsKvEntry(saved.getCreatedTime(),
                new StringDataEntry(ApiFeature.DB.getApiStateKey(), ApiUsageStateValue.ENABLED.name())));
        apiUsageStates.add(new BasicTsKvEntry(saved.getCreatedTime(),
                new StringDataEntry(ApiFeature.RE.getApiStateKey(), ApiUsageStateValue.ENABLED.name())));
        apiUsageStates.add(new BasicTsKvEntry(saved.getCreatedTime(),
                new StringDataEntry(ApiFeature.JS.getApiStateKey(), ApiUsageStateValue.ENABLED.name())));
        apiUsageStates.add(new BasicTsKvEntry(saved.getCreatedTime(),
                new StringDataEntry(ApiFeature.EMAIL.getApiStateKey(), ApiUsageStateValue.ENABLED.name())));
        apiUsageStates.add(new BasicTsKvEntry(saved.getCreatedTime(),
                new StringDataEntry(ApiFeature.SMS.getApiStateKey(), ApiUsageStateValue.ENABLED.name())));
        apiUsageStates.add(new BasicTsKvEntry(saved.getCreatedTime(),
                new StringDataEntry(ApiFeature.ALARM.getApiStateKey(), ApiUsageStateValue.ENABLED.name())));
        tsService.save(tenantId, saved.getId(), apiUsageStates, 0L);

        if (entityId.getEntityType() == EntityType.TENANT && !entityId.equals(TenantId.SYS_TENANT_ID)) {
            tenantId = (TenantId) entityId;
            Tenant tenant = tenantService.findTenantById(tenantId);
            TenantProfile tenantProfile = tenantProfileDao.findById(tenantId, tenant.getTenantProfileId().getId());
            TenantProfileConfiguration configuration = tenantProfile.getProfileData().getConfiguration();

            List<TsKvEntry> profileThresholds = new ArrayList<>();
            for (ApiUsageRecordKey key : ApiUsageRecordKey.values()) {
                profileThresholds.add(new BasicTsKvEntry(saved.getCreatedTime(), new LongDataEntry(key.getApiLimitKey(), configuration.getProfileThreshold(key))));
            }
            tsService.save(tenantId, saved.getId(), profileThresholds, 0L);
        }

        return saved;
    }

    @Override
    public ApiUsageState update(ApiUsageState apiUsageState) {
        log.trace("Executing save [{}]", apiUsageState.getTenantId());
        validateId(apiUsageState.getTenantId(), INCORRECT_TENANT_ID + apiUsageState.getTenantId());
        validateId(apiUsageState.getId(), "Can't save new usage state. Only update is allowed!");
        return apiUsageStateDao.save(apiUsageState.getTenantId(), apiUsageState);
    }

    @Override
    public ApiUsageState findTenantApiUsageState(TenantId tenantId) {
        log.trace("Executing findTenantUsageRecord, tenantId [{}]", tenantId);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        return apiUsageStateDao.findTenantApiUsageState(tenantId.getId());
    }

    @Override
    public ApiUsageState findApiUsageStateByEntityId(EntityId entityId) {
        validateId(entityId.getId(), "Invalid entity id");
        return apiUsageStateDao.findApiUsageStateByEntityId(entityId);
    }

    @Override
    public ApiUsageState findApiUsageStateById(TenantId tenantId, ApiUsageStateId id) {
        log.trace("Executing findApiUsageStateById, tenantId [{}], apiUsageStateId [{}]", tenantId, id);
        validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
        validateId(id, "Incorrect apiUsageStateId " + id);
        return apiUsageStateDao.findById(tenantId, id.getId());
    }

}
