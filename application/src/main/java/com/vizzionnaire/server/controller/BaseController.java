package com.vizzionnaire.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.Dashboard;
import com.vizzionnaire.server.common.data.DashboardInfo;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceInfo;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EntityType;
import com.vizzionnaire.server.common.data.EntityView;
import com.vizzionnaire.server.common.data.EntityViewInfo;
import com.vizzionnaire.server.common.data.HasTenantId;
import com.vizzionnaire.server.common.data.OtaPackage;
import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.TbResource;
import com.vizzionnaire.server.common.data.TbResourceInfo;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.TenantInfo;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.alarm.AlarmInfo;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.asset.AssetInfo;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.edge.EdgeEventType;
import com.vizzionnaire.server.common.data.edge.EdgeInfo;
import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.AlarmId;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DashboardId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.id.OtaPackageId;
import com.vizzionnaire.server.common.data.id.QueueId;
import com.vizzionnaire.server.common.data.id.RpcId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.TbResourceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.TenantProfileId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.id.WidgetTypeId;
import com.vizzionnaire.server.common.data.id.WidgetsBundleId;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.page.SortOrder;
import com.vizzionnaire.server.common.data.page.TimePageLink;
import com.vizzionnaire.server.common.data.plugin.ComponentDescriptor;
import com.vizzionnaire.server.common.data.plugin.ComponentType;
import com.vizzionnaire.server.common.data.queue.Queue;
import com.vizzionnaire.server.common.data.rpc.Rpc;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleChainType;
import com.vizzionnaire.server.common.data.rule.RuleNode;
import com.vizzionnaire.server.common.data.widget.WidgetTypeDetails;
import com.vizzionnaire.server.common.data.widget.WidgetsBundle;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.attributes.AttributesService;
import com.vizzionnaire.server.dao.audit.AuditLogService;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.dashboard.DashboardService;
import com.vizzionnaire.server.dao.device.ClaimDevicesService;
import com.vizzionnaire.server.dao.device.DeviceCredentialsService;
import com.vizzionnaire.server.dao.device.DeviceProfileService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.dao.exception.DataValidationException;
import com.vizzionnaire.server.dao.exception.IncorrectParameterException;
import com.vizzionnaire.server.dao.model.ModelConstants;
import com.vizzionnaire.server.dao.oauth2.OAuth2ConfigTemplateService;
import com.vizzionnaire.server.dao.oauth2.OAuth2Service;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.queue.QueueService;
import com.vizzionnaire.server.dao.relation.RelationService;
import com.vizzionnaire.server.dao.rpc.RpcService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.tenant.TbTenantProfileCache;
import com.vizzionnaire.server.dao.tenant.TenantProfileService;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.dao.widget.WidgetTypeService;
import com.vizzionnaire.server.dao.widget.WidgetsBundleService;
import com.vizzionnaire.server.exception.VizzionnaireErrorResponseHandler;
import com.vizzionnaire.server.queue.discovery.PartitionService;
import com.vizzionnaire.server.queue.provider.TbQueueProducerProvider;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.component.ComponentDiscoveryService;
import com.vizzionnaire.server.service.edge.EdgeNotificationService;
import com.vizzionnaire.server.service.edge.rpc.EdgeRpcService;
import com.vizzionnaire.server.service.entitiy.TbNotificationEntityService;
import com.vizzionnaire.server.service.ota.OtaPackageStateService;
import com.vizzionnaire.server.service.profile.TbDeviceProfileCache;
import com.vizzionnaire.server.service.resource.TbResourceService;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.service.security.permission.AccessControlService;
import com.vizzionnaire.server.service.security.permission.Operation;
import com.vizzionnaire.server.service.security.permission.Resource;
import com.vizzionnaire.server.service.state.DeviceStateService;
import com.vizzionnaire.server.service.sync.vc.EntitiesVersionControlService;
import com.vizzionnaire.server.service.telemetry.AlarmSubscriptionService;
import com.vizzionnaire.server.service.telemetry.TelemetrySubscriptionService;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.DeferredResult;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.vizzionnaire.server.controller.ControllerConstants.INCORRECT_TENANT_ID;
import static com.vizzionnaire.server.controller.UserController.YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION;
import static com.vizzionnaire.server.dao.service.Validator.validateId;

@Slf4j
@TbCoreComponent
public abstract class BaseController {

    /*Swagger UI description*/

    private static final ObjectMapper json = new ObjectMapper();

    @Autowired
    private VizzionnaireErrorResponseHandler errorResponseHandler;

    @Autowired
    protected AccessControlService accessControlService;

    @Autowired
    protected TenantService tenantService;

    @Autowired
    protected TenantProfileService tenantProfileService;

    @Autowired
    protected CustomerService customerService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected DeviceProfileService deviceProfileService;

    @Autowired
    protected AssetService assetService;

    @Autowired
    protected AlarmSubscriptionService alarmService;

    @Autowired
    protected DeviceCredentialsService deviceCredentialsService;

    @Autowired
    protected WidgetsBundleService widgetsBundleService;

    @Autowired
    protected WidgetTypeService widgetTypeService;

    @Autowired
    protected DashboardService dashboardService;

    @Autowired
    protected OAuth2Service oAuth2Service;

    @Autowired
    protected OAuth2ConfigTemplateService oAuth2ConfigTemplateService;

    @Autowired
    protected ComponentDiscoveryService componentDescriptorService;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected TbClusterService tbClusterService;

    @Autowired
    protected RelationService relationService;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    protected DeviceStateService deviceStateService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired
    protected TelemetrySubscriptionService tsSubService;

    @Autowired
    protected AttributesService attributesService;

    @Autowired
    protected ClaimDevicesService claimDevicesService;

    @Autowired
    protected PartitionService partitionService;

    @Autowired
    protected TbResourceService resourceService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected OtaPackageStateService otaPackageStateService;

    @Autowired
    protected RpcService rpcService;

    @Autowired
    protected TbQueueProducerProvider producerProvider;

    @Autowired
    protected TbTenantProfileCache tenantProfileCache;

    @Autowired
    protected TbDeviceProfileCache deviceProfileCache;

    @Autowired(required = false)
    protected EdgeService edgeService;

    @Autowired(required = false)
    protected EdgeNotificationService edgeNotificationService;

    @Autowired(required = false)
    protected EdgeRpcService edgeGrpcService;

    @Autowired
    protected TbNotificationEntityService notificationEntityService;

    @Autowired
    protected QueueService queueService;

    @Autowired
    protected EntitiesVersionControlService vcService;

    @Value("${server.log_controller_error_stack_trace}")
    @Getter
    private boolean logControllerErrorStackTrace;

    @Value("${edges.enabled}")
    @Getter
    protected boolean edgesEnabled;

    @ExceptionHandler(Exception.class)
    public void handleControllerException(Exception e, HttpServletResponse response) {
        VizzionnaireException vizzionnaireException = handleException(e);
        if (vizzionnaireException.getErrorCode() == VizzionnaireErrorCode.GENERAL && vizzionnaireException.getCause() instanceof Exception
                && StringUtils.equals(vizzionnaireException.getCause().getMessage(), vizzionnaireException.getMessage())) {
            e = (Exception) vizzionnaireException.getCause();
        } else {
            e = vizzionnaireException;
        }
        errorResponseHandler.handle(e, response);
    }

    @ExceptionHandler(VizzionnaireException.class)
    public void handleVizzionnaireException(VizzionnaireException ex, HttpServletResponse response) {
        errorResponseHandler.handle(ex, response);
    }

    /**
     * @deprecated Exceptions that are not of {@link VizzionnaireException} type
     * are now caught and mapped to {@link VizzionnaireException} by
     * {@link ExceptionHandler} {@link BaseController#handleControllerException(Exception, HttpServletResponse)}
     * which basically acts like the following boilerplate:
     * {@code
     *  try {
     *      someExceptionThrowingMethod();
     *  } catch (Exception e) {
     *      throw handleException(e);
     *  }
     * }
     * */
    @Deprecated
    VizzionnaireException handleException(Exception exception) {
        return handleException(exception, true);
    }

    private VizzionnaireException handleException(Exception exception, boolean logException) {
        if (logException && logControllerErrorStackTrace) {
            log.error("Error [{}]", exception.getMessage(), exception);
        }

        String cause = "";
        if (exception.getCause() != null) {
            cause = exception.getCause().getClass().getCanonicalName();
        }

        if (exception instanceof VizzionnaireException) {
            return (VizzionnaireException) exception;
        } else if (exception instanceof IllegalArgumentException || exception instanceof IncorrectParameterException
                || exception instanceof DataValidationException || cause.contains("IncorrectParameterException")) {
            return new VizzionnaireException(exception.getMessage(), VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        } else if (exception instanceof MessagingException) {
            return new VizzionnaireException("Unable to send mail: " + exception.getMessage(), VizzionnaireErrorCode.GENERAL);
        } else if (exception instanceof AsyncRequestTimeoutException) {
            return new VizzionnaireException("Request timeout", VizzionnaireErrorCode.GENERAL);
        } else {
            return new VizzionnaireException(exception.getMessage(), exception, VizzionnaireErrorCode.GENERAL);
        }
    }

    /**
     * Handles validation error for controller method arguments annotated with @{@link javax.validation.Valid}
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationError(MethodArgumentNotValidException e, HttpServletResponse response) {
        String errorMessage = "Validation error: " + e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        VizzionnaireException vizzionnaireException = new VizzionnaireException(errorMessage, VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        handleVizzionnaireException(vizzionnaireException, response);
    }

    <T> T checkNotNull(T reference) throws VizzionnaireException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    <T> T checkNotNull(T reference, String notFoundMessage) throws VizzionnaireException {
        if (reference == null) {
            throw new VizzionnaireException(notFoundMessage, VizzionnaireErrorCode.ITEM_NOT_FOUND);
        }
        return reference;
    }

    <T> T checkNotNull(Optional<T> reference) throws VizzionnaireException {
        return checkNotNull(reference, "Requested item wasn't found!");
    }

    <T> T checkNotNull(Optional<T> reference, String notFoundMessage) throws VizzionnaireException {
        if (reference.isPresent()) {
            return reference.get();
        } else {
            throw new VizzionnaireException(notFoundMessage, VizzionnaireErrorCode.ITEM_NOT_FOUND);
        }
    }

    void checkParameter(String name, String param) throws VizzionnaireException {
        if (StringUtils.isEmpty(param)) {
            throw new VizzionnaireException("Parameter '" + name + "' can't be empty!", VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        }
    }

    void checkArrayParameter(String name, String[] params) throws VizzionnaireException {
        if (params == null || params.length == 0) {
            throw new VizzionnaireException("Parameter '" + name + "' can't be empty!", VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
        } else {
            for (String param : params) {
                checkParameter(name, param);
            }
        }
    }

    UUID toUUID(String id) throws VizzionnaireException {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw handleException(e, false);
        }
    }

    PageLink createPageLink(int pageSize, int page, String textSearch, String sortProperty, String sortOrder) throws VizzionnaireException {
        if (!StringUtils.isEmpty(sortProperty)) {
            SortOrder.Direction direction = SortOrder.Direction.ASC;
            if (!StringUtils.isEmpty(sortOrder)) {
                try {
                    direction = SortOrder.Direction.valueOf(sortOrder.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new VizzionnaireException("Unsupported sort order '" + sortOrder + "'! Only 'ASC' or 'DESC' types are allowed.", VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
                }
            }
            SortOrder sort = new SortOrder(sortProperty, direction);
            return new PageLink(pageSize, page, textSearch, sort);
        } else {
            return new PageLink(pageSize, page, textSearch);
        }
    }

    TimePageLink createTimePageLink(int pageSize, int page, String textSearch,
                                    String sortProperty, String sortOrder, Long startTime, Long endTime) throws VizzionnaireException {
        PageLink pageLink = this.createPageLink(pageSize, page, textSearch, sortProperty, sortOrder);
        return new TimePageLink(pageLink, startTime, endTime);
    }

    protected SecurityUser getCurrentUser() throws VizzionnaireException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
            return (SecurityUser) authentication.getPrincipal();
        } else {
            throw new VizzionnaireException("You aren't authorized to perform this operation!", VizzionnaireErrorCode.AUTHENTICATION);
        }
    }

    Tenant checkTenantId(TenantId tenantId, Operation operation) throws VizzionnaireException {
        try {
            validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
            Tenant tenant = tenantService.findTenantById(tenantId);
            checkNotNull(tenant, "Tenant with id [" + tenantId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT, operation, tenantId, tenant);
            return tenant;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    TenantInfo checkTenantInfoId(TenantId tenantId, Operation operation) throws VizzionnaireException {
        try {
            validateId(tenantId, INCORRECT_TENANT_ID + tenantId);
            TenantInfo tenant = tenantService.findTenantInfoById(tenantId);
            checkNotNull(tenant, "Tenant with id [" + tenantId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT, operation, tenantId, tenant);
            return tenant;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    TenantProfile checkTenantProfileId(TenantProfileId tenantProfileId, Operation operation) throws VizzionnaireException {
        try {
            validateId(tenantProfileId, "Incorrect tenantProfileId " + tenantProfileId);
            TenantProfile tenantProfile = tenantProfileService.findTenantProfileById(getTenantId(), tenantProfileId);
            checkNotNull(tenantProfile, "Tenant profile with id [" + tenantProfileId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.TENANT_PROFILE, operation);
            return tenantProfile;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected TenantId getTenantId() throws VizzionnaireException {
        return getCurrentUser().getTenantId();
    }

    Customer checkCustomerId(CustomerId customerId, Operation operation) throws VizzionnaireException {
        try {
            validateId(customerId, "Incorrect customerId " + customerId);
            Customer customer = customerService.findCustomerById(getTenantId(), customerId);
            checkNotNull(customer, "Customer with id [" + customerId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.CUSTOMER, operation, customerId, customer);
            return customer;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    User checkUserId(UserId userId, Operation operation) throws VizzionnaireException {
        try {
            validateId(userId, "Incorrect userId " + userId);
            User user = userService.findUserById(getCurrentUser().getTenantId(), userId);
            checkNotNull(user, "User with id [" + userId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.USER, operation, userId, user);
            return user;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected <I extends EntityId, T extends HasTenantId> void checkEntity(I entityId, T entity, Resource resource) throws VizzionnaireException {
        if (entityId == null) {
            accessControlService
                    .checkPermission(getCurrentUser(), resource, Operation.CREATE, null, entity);
        } else {
            checkEntityId(entityId, Operation.WRITE);
        }
    }

    protected void checkEntityId(EntityId entityId, Operation operation) throws VizzionnaireException {
        try {
            if (entityId == null) {
                throw new VizzionnaireException("Parameter entityId can't be empty!", VizzionnaireErrorCode.BAD_REQUEST_PARAMS);
            }
            validateId(entityId.getId(), "Incorrect entityId " + entityId);
            switch (entityId.getEntityType()) {
                case ALARM:
                    checkAlarmId(new AlarmId(entityId.getId()), operation);
                    return;
                case DEVICE:
                    checkDeviceId(new DeviceId(entityId.getId()), operation);
                    return;
                case DEVICE_PROFILE:
                    checkDeviceProfileId(new DeviceProfileId(entityId.getId()), operation);
                    return;
                case CUSTOMER:
                    checkCustomerId(new CustomerId(entityId.getId()), operation);
                    return;
                case TENANT:
                    checkTenantId(TenantId.fromUUID(entityId.getId()), operation);
                    return;
                case TENANT_PROFILE:
                    checkTenantProfileId(new TenantProfileId(entityId.getId()), operation);
                    return;
                case RULE_CHAIN:
                    checkRuleChain(new RuleChainId(entityId.getId()), operation);
                    return;
                case RULE_NODE:
                    checkRuleNode(new RuleNodeId(entityId.getId()), operation);
                    return;
                case ASSET:
                    checkAssetId(new AssetId(entityId.getId()), operation);
                    return;
                case DASHBOARD:
                    checkDashboardId(new DashboardId(entityId.getId()), operation);
                    return;
                case USER:
                    checkUserId(new UserId(entityId.getId()), operation);
                    return;
                case ENTITY_VIEW:
                    checkEntityViewId(new EntityViewId(entityId.getId()), operation);
                    return;
                case EDGE:
                    checkEdgeId(new EdgeId(entityId.getId()), operation);
                    return;
                case WIDGETS_BUNDLE:
                    checkWidgetsBundleId(new WidgetsBundleId(entityId.getId()), operation);
                    return;
                case WIDGET_TYPE:
                    checkWidgetTypeId(new WidgetTypeId(entityId.getId()), operation);
                    return;
                case TB_RESOURCE:
                    checkResourceId(new TbResourceId(entityId.getId()), operation);
                    return;
                case OTA_PACKAGE:
                    checkOtaPackageId(new OtaPackageId(entityId.getId()), operation);
                    return;
                case QUEUE:
                    checkQueueId(new QueueId(entityId.getId()), operation);
                    return;
                default:
                    throw new IllegalArgumentException("Unsupported entity type: " + entityId.getEntityType());
            }
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Device checkDeviceId(DeviceId deviceId, Operation operation) throws VizzionnaireException {
        try {
            validateId(deviceId, "Incorrect deviceId " + deviceId);
            Device device = deviceService.findDeviceById(getCurrentUser().getTenantId(), deviceId);
            checkNotNull(device, "Device with id [" + deviceId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE, operation, deviceId, device);
            return device;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    DeviceInfo checkDeviceInfoId(DeviceId deviceId, Operation operation) throws VizzionnaireException {
        try {
            validateId(deviceId, "Incorrect deviceId " + deviceId);
            DeviceInfo device = deviceService.findDeviceInfoById(getCurrentUser().getTenantId(), deviceId);
            checkNotNull(device, "Device with id [" + deviceId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE, operation, deviceId, device);
            return device;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    DeviceProfile checkDeviceProfileId(DeviceProfileId deviceProfileId, Operation operation) throws VizzionnaireException {
        try {
            validateId(deviceProfileId, "Incorrect deviceProfileId " + deviceProfileId);
            DeviceProfile deviceProfile = deviceProfileService.findDeviceProfileById(getCurrentUser().getTenantId(), deviceProfileId);
            checkNotNull(deviceProfile, "Device profile with id [" + deviceProfileId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.DEVICE_PROFILE, operation, deviceProfileId, deviceProfile);
            return deviceProfile;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected EntityView checkEntityViewId(EntityViewId entityViewId, Operation operation) throws VizzionnaireException {
        try {
            validateId(entityViewId, "Incorrect entityViewId " + entityViewId);
            EntityView entityView = entityViewService.findEntityViewById(getCurrentUser().getTenantId(), entityViewId);
            checkNotNull(entityView, "Entity view with id [" + entityViewId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.ENTITY_VIEW, operation, entityViewId, entityView);
            return entityView;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    EntityViewInfo checkEntityViewInfoId(EntityViewId entityViewId, Operation operation) throws VizzionnaireException {
        try {
            validateId(entityViewId, "Incorrect entityViewId " + entityViewId);
            EntityViewInfo entityView = entityViewService.findEntityViewInfoById(getCurrentUser().getTenantId(), entityViewId);
            checkNotNull(entityView, "Entity view with id [" + entityViewId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.ENTITY_VIEW, operation, entityViewId, entityView);
            return entityView;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Asset checkAssetId(AssetId assetId, Operation operation) throws VizzionnaireException {
        try {
            validateId(assetId, "Incorrect assetId " + assetId);
            Asset asset = assetService.findAssetById(getCurrentUser().getTenantId(), assetId);
            checkNotNull(asset, "Asset with id [" + assetId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.ASSET, operation, assetId, asset);
            return asset;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    AssetInfo checkAssetInfoId(AssetId assetId, Operation operation) throws VizzionnaireException {
        try {
            validateId(assetId, "Incorrect assetId " + assetId);
            AssetInfo asset = assetService.findAssetInfoById(getCurrentUser().getTenantId(), assetId);
            checkNotNull(asset, "Asset with id [" + assetId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.ASSET, operation, assetId, asset);
            return asset;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Alarm checkAlarmId(AlarmId alarmId, Operation operation) throws VizzionnaireException {
        try {
            validateId(alarmId, "Incorrect alarmId " + alarmId);
            Alarm alarm = alarmService.findAlarmByIdAsync(getCurrentUser().getTenantId(), alarmId).get();
            checkNotNull(alarm, "Alarm with id [" + alarmId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.ALARM, operation, alarmId, alarm);
            return alarm;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    AlarmInfo checkAlarmInfoId(AlarmId alarmId, Operation operation) throws VizzionnaireException {
        try {
            validateId(alarmId, "Incorrect alarmId " + alarmId);
            AlarmInfo alarmInfo = alarmService.findAlarmInfoByIdAsync(getCurrentUser().getTenantId(), alarmId).get();
            checkNotNull(alarmInfo, "Alarm with id [" + alarmId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.ALARM, operation, alarmId, alarmInfo);
            return alarmInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    WidgetsBundle checkWidgetsBundleId(WidgetsBundleId widgetsBundleId, Operation operation) throws VizzionnaireException {
        try {
            validateId(widgetsBundleId, "Incorrect widgetsBundleId " + widgetsBundleId);
            WidgetsBundle widgetsBundle = widgetsBundleService.findWidgetsBundleById(getCurrentUser().getTenantId(), widgetsBundleId);
            checkNotNull(widgetsBundle, "Widgets bundle with id [" + widgetsBundleId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.WIDGETS_BUNDLE, operation, widgetsBundleId, widgetsBundle);
            return widgetsBundle;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    WidgetTypeDetails checkWidgetTypeId(WidgetTypeId widgetTypeId, Operation operation) throws VizzionnaireException {
        try {
            validateId(widgetTypeId, "Incorrect widgetTypeId " + widgetTypeId);
            WidgetTypeDetails widgetTypeDetails = widgetTypeService.findWidgetTypeDetailsById(getCurrentUser().getTenantId(), widgetTypeId);
            checkNotNull(widgetTypeDetails, "Widget type with id [" + widgetTypeId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.WIDGET_TYPE, operation, widgetTypeId, widgetTypeDetails);
            return widgetTypeDetails;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Dashboard checkDashboardId(DashboardId dashboardId, Operation operation) throws VizzionnaireException {
        try {
            validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
            Dashboard dashboard = dashboardService.findDashboardById(getCurrentUser().getTenantId(), dashboardId);
            checkNotNull(dashboard, "Dashboard with id [" + dashboardId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.DASHBOARD, operation, dashboardId, dashboard);
            return dashboard;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Edge checkEdgeId(EdgeId edgeId, Operation operation) throws VizzionnaireException {
        try {
            validateId(edgeId, "Incorrect edgeId " + edgeId);
            Edge edge = edgeService.findEdgeById(getTenantId(), edgeId);
            checkNotNull(edge, "Edge with id [" + edgeId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.EDGE, operation, edgeId, edge);
            return edge;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    EdgeInfo checkEdgeInfoId(EdgeId edgeId, Operation operation) throws VizzionnaireException {
        try {
            validateId(edgeId, "Incorrect edgeId " + edgeId);
            EdgeInfo edge = edgeService.findEdgeInfoById(getCurrentUser().getTenantId(), edgeId);
            checkNotNull(edge, "Edge with id [" + edgeId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.EDGE, operation, edgeId, edge);
            return edge;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    DashboardInfo checkDashboardInfoId(DashboardId dashboardId, Operation operation) throws VizzionnaireException {
        try {
            validateId(dashboardId, "Incorrect dashboardId " + dashboardId);
            DashboardInfo dashboardInfo = dashboardService.findDashboardInfoById(getCurrentUser().getTenantId(), dashboardId);
            checkNotNull(dashboardInfo, "Dashboard with id [" + dashboardId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.DASHBOARD, operation, dashboardId, dashboardInfo);
            return dashboardInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    ComponentDescriptor checkComponentDescriptorByClazz(String clazz) throws VizzionnaireException {
        try {
            log.debug("[{}] Lookup component descriptor", clazz);
            return checkNotNull(componentDescriptorService.getComponent(clazz));
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByType(ComponentType type, RuleChainType ruleChainType) throws VizzionnaireException {
        try {
            log.debug("[{}] Lookup component descriptors", type);
            return componentDescriptorService.getComponents(type, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    List<ComponentDescriptor> checkComponentDescriptorsByTypes(Set<ComponentType> types, RuleChainType ruleChainType) throws VizzionnaireException {
        try {
            log.debug("[{}] Lookup component descriptors", types);
            return componentDescriptorService.getComponents(types, ruleChainType);
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected RuleChain checkRuleChain(RuleChainId ruleChainId, Operation operation) throws VizzionnaireException {
        validateId(ruleChainId, "Incorrect ruleChainId " + ruleChainId);
        RuleChain ruleChain = ruleChainService.findRuleChainById(getCurrentUser().getTenantId(), ruleChainId);
        checkNotNull(ruleChain, "Rule chain with id [" + ruleChainId + "] is not found");
        accessControlService.checkPermission(getCurrentUser(), Resource.RULE_CHAIN, operation, ruleChainId, ruleChain);
        return ruleChain;
    }

    protected RuleNode checkRuleNode(RuleNodeId ruleNodeId, Operation operation) throws VizzionnaireException {
        validateId(ruleNodeId, "Incorrect ruleNodeId " + ruleNodeId);
        RuleNode ruleNode = ruleChainService.findRuleNodeById(getTenantId(), ruleNodeId);
        checkNotNull(ruleNode, "Rule node with id [" + ruleNodeId + "] is not found");
        checkRuleChain(ruleNode.getRuleChainId(), operation);
        return ruleNode;
    }

    TbResource checkResourceId(TbResourceId resourceId, Operation operation) throws VizzionnaireException {
        try {
            validateId(resourceId, "Incorrect resourceId " + resourceId);
            TbResource resource = resourceService.findResourceById(getCurrentUser().getTenantId(), resourceId);
            checkNotNull(resource, "Resource with id [" + resourceId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.TB_RESOURCE, operation, resourceId, resource);
            return resource;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    TbResourceInfo checkResourceInfoId(TbResourceId resourceId, Operation operation) throws VizzionnaireException {
        try {
            validateId(resourceId, "Incorrect resourceId " + resourceId);
            TbResourceInfo resourceInfo = resourceService.findResourceInfoById(getCurrentUser().getTenantId(), resourceId);
            checkNotNull(resourceInfo, "Resource with id [" + resourceId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.TB_RESOURCE, operation, resourceId, resourceInfo);
            return resourceInfo;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    OtaPackage checkOtaPackageId(OtaPackageId otaPackageId, Operation operation) throws VizzionnaireException {
        try {
            validateId(otaPackageId, "Incorrect otaPackageId " + otaPackageId);
            OtaPackage otaPackage = otaPackageService.findOtaPackageById(getCurrentUser().getTenantId(), otaPackageId);
            checkNotNull(otaPackage, "OTA package with id [" + otaPackageId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.OTA_PACKAGE, operation, otaPackageId, otaPackage);
            return otaPackage;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    OtaPackageInfo checkOtaPackageInfoId(OtaPackageId otaPackageId, Operation operation) throws VizzionnaireException {
        try {
            validateId(otaPackageId, "Incorrect otaPackageId " + otaPackageId);
            OtaPackageInfo otaPackageIn = otaPackageService.findOtaPackageInfoById(getCurrentUser().getTenantId(), otaPackageId);
            checkNotNull(otaPackageIn, "OTA package with id [" + otaPackageId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.OTA_PACKAGE, operation, otaPackageId, otaPackageIn);
            return otaPackageIn;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    Rpc checkRpcId(RpcId rpcId, Operation operation) throws VizzionnaireException {
        try {
            validateId(rpcId, "Incorrect rpcId " + rpcId);
            Rpc rpc = rpcService.findById(getCurrentUser().getTenantId(), rpcId);
            checkNotNull(rpc, "RPC with id [" + rpcId + "] is not found");
            accessControlService.checkPermission(getCurrentUser(), Resource.RPC, operation, rpcId, rpc);
            return rpc;
        } catch (Exception e) {
            throw handleException(e, false);
        }
    }

    protected Queue checkQueueId(QueueId queueId, Operation operation) throws VizzionnaireException {
        validateId(queueId, "Incorrect queueId " + queueId);
        Queue queue = queueService.findQueueById(getCurrentUser().getTenantId(), queueId);
        checkNotNull(queue);
        accessControlService.checkPermission(getCurrentUser(), Resource.QUEUE, operation, queueId, queue);
        TenantId tenantId = getTenantId();
        if (queue.getTenantId().isNullUid() && !tenantId.isNullUid()) {
            TenantProfile tenantProfile = tenantProfileCache.get(tenantId);
            if (tenantProfile.isIsolatedTbRuleEngine()) {
                throw new VizzionnaireException(YOU_DON_T_HAVE_PERMISSION_TO_PERFORM_THIS_OPERATION,
                        VizzionnaireErrorCode.PERMISSION_DENIED);
            }
        }
        return queue;
    }

    protected <I extends EntityId> I emptyId(EntityType entityType) {
        return (I) EntityIdFactory.getByTypeAndUuid(entityType, ModelConstants.NULL_UUID);
    }

    public static Exception toException(Throwable error) {
        return error != null ? (Exception.class.isInstance(error) ? (Exception) error : new Exception(error)) : null;
    }

    protected void sendEntityNotificationMsg(TenantId tenantId, EntityId entityId, EdgeEventActionType action) {
        sendNotificationMsgToEdge(tenantId, null, entityId, null, null, action);
    }

    protected void sendEntityAssignToEdgeNotificationMsg(TenantId tenantId, EdgeId edgeId, EntityId entityId, EdgeEventActionType action) {
        sendNotificationMsgToEdge(tenantId, edgeId, entityId, null, null, action);
    }

    private void sendNotificationMsgToEdge(TenantId tenantId, EdgeId edgeId, EntityId entityId, String body, EdgeEventType type, EdgeEventActionType action) {
        tbClusterService.sendNotificationMsgToEdge(tenantId, edgeId, entityId, body, type, action);
    }

    protected void processDashboardIdFromAdditionalInfo(ObjectNode additionalInfo, String requiredFields) throws VizzionnaireException {
        String dashboardId = additionalInfo.has(requiredFields) ? additionalInfo.get(requiredFields).asText() : null;
        if (dashboardId != null && !dashboardId.equals("null")) {
            if (dashboardService.findDashboardById(getTenantId(), new DashboardId(UUID.fromString(dashboardId))) == null) {
                additionalInfo.remove(requiredFields);
            }
        }
    }

    protected MediaType parseMediaType(String contentType) {
        try {
            return MediaType.parseMediaType(contentType);
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

    protected <T> DeferredResult<T> wrapFuture(ListenableFuture<T> future) {
        final DeferredResult<T> deferredResult = new DeferredResult<>();
        Futures.addCallback(future, new FutureCallback<>() {
            @Override
            public void onSuccess(T result) {
                deferredResult.setResult(result);
            }

            @Override
            public void onFailure(Throwable t) {
                deferredResult.setErrorResult(t);
            }
        }, MoreExecutors.directExecutor());
        return deferredResult;
    }
}
