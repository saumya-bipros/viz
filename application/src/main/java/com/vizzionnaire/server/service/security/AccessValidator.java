package com.vizzionnaire.server.service.security;

import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vizzionnaire.common.util.VizzionnaireThreadFactory;
import com.vizzionnaire.server.common.data.ApiUsageState;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.EntityView;
import com.vizzionnaire.server.common.data.OtaPackageInfo;
import com.vizzionnaire.server.common.data.TbResourceInfo;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.exception.VizzionnaireException;
import com.vizzionnaire.server.common.data.id.ApiUsageStateId;
import com.vizzionnaire.server.common.data.id.AssetId;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.DeviceProfileId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.EntityIdFactory;
import com.vizzionnaire.server.common.data.id.EntityViewId;
import com.vizzionnaire.server.common.data.id.OtaPackageId;
import com.vizzionnaire.server.common.data.id.RpcId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.TbResourceId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.id.UserId;
import com.vizzionnaire.server.common.data.rpc.Rpc;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.rule.RuleNode;
import com.vizzionnaire.server.controller.HttpValidationCallback;
import com.vizzionnaire.server.dao.alarm.AlarmService;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.device.DeviceProfileService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.dao.exception.IncorrectParameterException;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.resource.ResourceService;
import com.vizzionnaire.server.dao.rpc.RpcService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.usagerecord.ApiUsageStateService;
import com.vizzionnaire.server.dao.user.UserService;
import com.vizzionnaire.server.service.security.model.SecurityUser;
import com.vizzionnaire.server.service.security.permission.AccessControlService;
import com.vizzionnaire.server.service.security.permission.Operation;
import com.vizzionnaire.server.service.security.permission.Resource;
import com.vizzionnaire.server.service.telemetry.exception.ToErrorResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

/**
 * Created by ashvayka on 27.03.18.
 */
@Component
public class AccessValidator {

    public static final String ONLY_SYSTEM_ADMINISTRATOR_IS_ALLOWED_TO_PERFORM_THIS_OPERATION = "Only system administrator is allowed to perform this operation!";
    public static final String CUSTOMER_USER_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION = "Customer user is not allowed to perform this operation!";
    public static final String SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION = "System administrator is not allowed to perform this operation!";
    public static final String DEVICE_WITH_REQUESTED_ID_NOT_FOUND = "Device with requested id wasn't found!";
    public static final String EDGE_WITH_REQUESTED_ID_NOT_FOUND = "Edge with requested id wasn't found!";
    public static final String ENTITY_VIEW_WITH_REQUESTED_ID_NOT_FOUND = "Entity-view with requested id wasn't found!";

    @Autowired
    protected TenantService tenantService;

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
    protected AlarmService alarmService;

    @Autowired
    protected RuleChainService ruleChainService;

    @Autowired
    protected EntityViewService entityViewService;

    @Autowired(required = false)
    protected EdgeService edgeService;

    @Autowired
    protected AccessControlService accessControlService;

    @Autowired
    protected ApiUsageStateService apiUsageStateService;

    @Autowired
    protected ResourceService resourceService;

    @Autowired
    protected OtaPackageService otaPackageService;

    @Autowired
    protected RpcService rpcService;

    private ExecutorService executor;

    @PostConstruct
    public void initExecutor() {
        executor = Executors.newSingleThreadExecutor(VizzionnaireThreadFactory.forName("access-validator"));
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public DeferredResult<ResponseEntity> validateEntityAndCallback(SecurityUser currentUser, Operation operation, String entityType, String entityIdStr,
                                                                    ThreeConsumer<DeferredResult<ResponseEntity>, TenantId, EntityId> onSuccess) throws VizzionnaireException {
        return validateEntityAndCallback(currentUser, operation, entityType, entityIdStr, onSuccess, (result, t) -> handleError(t, result, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public DeferredResult<ResponseEntity> validateEntityAndCallback(SecurityUser currentUser, Operation operation, String entityType, String entityIdStr,
                                                                    ThreeConsumer<DeferredResult<ResponseEntity>, TenantId, EntityId> onSuccess,
                                                                    BiConsumer<DeferredResult<ResponseEntity>, Throwable> onFailure) throws VizzionnaireException {
        return validateEntityAndCallback(currentUser, operation, EntityIdFactory.getByTypeAndId(entityType, entityIdStr),
                onSuccess, onFailure);
    }

    public DeferredResult<ResponseEntity> validateEntityAndCallback(SecurityUser currentUser, Operation operation, EntityId entityId,
                                                                    ThreeConsumer<DeferredResult<ResponseEntity>, TenantId, EntityId> onSuccess) throws VizzionnaireException {
        return validateEntityAndCallback(currentUser, operation, entityId, onSuccess, (result, t) -> handleError(t, result, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public DeferredResult<ResponseEntity> validateEntityAndCallback(SecurityUser currentUser, Operation operation, EntityId entityId,
                                                                    ThreeConsumer<DeferredResult<ResponseEntity>, TenantId, EntityId> onSuccess,
                                                                    BiConsumer<DeferredResult<ResponseEntity>, Throwable> onFailure) throws VizzionnaireException {

        final DeferredResult<ResponseEntity> response = new DeferredResult<>();

        validate(currentUser, operation, entityId, new HttpValidationCallback(response,
                new FutureCallback<DeferredResult<ResponseEntity>>() {
                    @Override
                    public void onSuccess(@Nullable DeferredResult<ResponseEntity> result) {
                        try {
                            onSuccess.accept(response, currentUser.getTenantId(), entityId);
                        } catch (Exception e) {
                            onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        onFailure.accept(response, t);
                    }
                }));

        return response;
    }

    public void validate(SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        switch (entityId.getEntityType()) {
            case DEVICE:
                validateDevice(currentUser, operation, entityId, callback);
                return;
            case DEVICE_PROFILE:
                validateDeviceProfile(currentUser, operation, entityId, callback);
                return;
            case ASSET:
                validateAsset(currentUser, operation, entityId, callback);
                return;
            case RULE_CHAIN:
                validateRuleChain(currentUser, operation, entityId, callback);
                return;
            case CUSTOMER:
                validateCustomer(currentUser, operation, entityId, callback);
                return;
            case TENANT:
                validateTenant(currentUser, operation, entityId, callback);
                return;
            case TENANT_PROFILE:
                validateTenantProfile(currentUser, operation, entityId, callback);
                return;
            case USER:
                validateUser(currentUser, operation, entityId, callback);
                return;
            case ENTITY_VIEW:
                validateEntityView(currentUser, operation, entityId, callback);
                return;
            case EDGE:
                validateEdge(currentUser, operation, entityId, callback);
                return;
            case API_USAGE_STATE:
                validateApiUsageState(currentUser, operation, entityId, callback);
                return;
            case TB_RESOURCE:
                validateResource(currentUser, operation, entityId, callback);
                return;
            case OTA_PACKAGE:
                validateOtaPackage(currentUser, operation, entityId, callback);
                return;
            case RPC:
                validateRpc(currentUser, operation, entityId, callback);
                return;
            default:
                //TODO: add support of other entities
                throw new IllegalStateException("Not Implemented!");
        }
    }

    private void validateDevice(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<Device> deviceFuture = deviceService.findDeviceByIdAsync(currentUser.getTenantId(), new DeviceId(entityId.getId()));
            Futures.addCallback(deviceFuture, getCallback(callback, device -> {
                if (device == null) {
                    return ValidationResult.entityNotFound(DEVICE_WITH_REQUESTED_ID_NOT_FOUND);
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.DEVICE, operation, entityId, device);
                    } catch (VizzionnaireException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(device);
                }
            }), executor);
        }
    }

    private void validateRpc(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        ListenableFuture<Rpc> rpcFurure = rpcService.findRpcByIdAsync(currentUser.getTenantId(), new RpcId(entityId.getId()));
        Futures.addCallback(rpcFurure, getCallback(callback, rpc -> {
            if (rpc == null) {
                return ValidationResult.entityNotFound("Rpc with requested id wasn't found!");
            } else {
                try {
                    accessControlService.checkPermission(currentUser, Resource.RPC, operation, entityId, rpc);
                } catch (VizzionnaireException e) {
                    return ValidationResult.accessDenied(e.getMessage());
                }
                return ValidationResult.ok(rpc);
            }
        }), executor);
    }

    private void validateDeviceProfile(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            DeviceProfile deviceProfile = deviceProfileService.findDeviceProfileById(currentUser.getTenantId(), new DeviceProfileId(entityId.getId()));
            if (deviceProfile == null) {
                callback.onSuccess(ValidationResult.entityNotFound("Device profile with requested id wasn't found!"));
            } else {
                try {
                    accessControlService.checkPermission(currentUser, Resource.DEVICE_PROFILE, operation, entityId, deviceProfile);
                } catch (VizzionnaireException e) {
                    callback.onSuccess(ValidationResult.accessDenied(e.getMessage()));
                }
                callback.onSuccess(ValidationResult.ok(deviceProfile));
            }
        }
    }

    private void validateApiUsageState(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            if (!operation.equals(Operation.READ_TELEMETRY)) {
                callback.onSuccess(ValidationResult.accessDenied("Allowed only READ_TELEMETRY operation!"));
            }
            ApiUsageState apiUsageState = apiUsageStateService.findApiUsageStateById(currentUser.getTenantId(), new ApiUsageStateId(entityId.getId()));
            if (apiUsageState == null) {
                callback.onSuccess(ValidationResult.entityNotFound("Api Usage State with requested id wasn't found!"));
            } else {
                try {
                    accessControlService.checkPermission(currentUser, Resource.API_USAGE_STATE, operation, entityId, apiUsageState);
                } catch (VizzionnaireException e) {
                    callback.onSuccess(ValidationResult.accessDenied(e.getMessage()));
                }
                callback.onSuccess(ValidationResult.ok(apiUsageState));
            }
        }
    }

    private void validateOtaPackage(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            OtaPackageInfo otaPackage = otaPackageService.findOtaPackageInfoById(currentUser.getTenantId(), new OtaPackageId(entityId.getId()));
            if (otaPackage == null) {
                callback.onSuccess(ValidationResult.entityNotFound("OtaPackage with requested id wasn't found!"));
            } else {
                try {
                    accessControlService.checkPermission(currentUser, Resource.OTA_PACKAGE, operation, entityId, otaPackage);
                } catch (VizzionnaireException e) {
                    callback.onSuccess(ValidationResult.accessDenied(e.getMessage()));
                }
                callback.onSuccess(ValidationResult.ok(otaPackage));
            }
        }
    }

    private void validateResource(SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        ListenableFuture<TbResourceInfo> resourceFuture = resourceService.findResourceInfoByIdAsync(currentUser.getTenantId(), new TbResourceId(entityId.getId()));
        Futures.addCallback(resourceFuture, getCallback(callback, resource -> {
            if (resource == null) {
                return ValidationResult.entityNotFound("Resource with requested id wasn't found!");
            } else {
                try {
                    accessControlService.checkPermission(currentUser, Resource.TB_RESOURCE, operation, entityId, resource);
                } catch (VizzionnaireException e) {
                    return ValidationResult.accessDenied(e.getMessage());
                }
                return ValidationResult.ok(resource);
            }
        }), executor);
    }

    private void validateAsset(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<Asset> assetFuture = assetService.findAssetByIdAsync(currentUser.getTenantId(), new AssetId(entityId.getId()));
            Futures.addCallback(assetFuture, getCallback(callback, asset -> {
                if (asset == null) {
                    return ValidationResult.entityNotFound("Asset with requested id wasn't found!");
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.ASSET, operation, entityId, asset);
                    } catch (VizzionnaireException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(asset);
                }
            }), executor);
        }
    }

    private void validateRuleChain(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isCustomerUser()) {
            callback.onSuccess(ValidationResult.accessDenied(CUSTOMER_USER_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<RuleChain> ruleChainFuture = ruleChainService.findRuleChainByIdAsync(currentUser.getTenantId(), new RuleChainId(entityId.getId()));
            Futures.addCallback(ruleChainFuture, getCallback(callback, ruleChain -> {
                if (ruleChain == null) {
                    return ValidationResult.entityNotFound("Rule chain with requested id wasn't found!");
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.RULE_CHAIN, operation, entityId, ruleChain);
                    } catch (VizzionnaireException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(ruleChain);
                }
            }), executor);
        }
    }

    private void validateRule(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isCustomerUser()) {
            callback.onSuccess(ValidationResult.accessDenied(CUSTOMER_USER_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<RuleNode> ruleNodeFuture = ruleChainService.findRuleNodeByIdAsync(currentUser.getTenantId(), new RuleNodeId(entityId.getId()));
            Futures.addCallback(ruleNodeFuture, getCallback(callback, ruleNodeTmp -> {
                RuleNode ruleNode = ruleNodeTmp;
                if (ruleNode == null) {
                    return ValidationResult.entityNotFound("Rule node with requested id wasn't found!");
                } else if (ruleNode.getRuleChainId() == null) {
                    return ValidationResult.entityNotFound("Rule chain with requested node id wasn't found!");
                } else {
                    //TODO: make async
                    RuleChain ruleChain = ruleChainService.findRuleChainById(currentUser.getTenantId(), ruleNode.getRuleChainId());
                    try {
                        accessControlService.checkPermission(currentUser, Resource.RULE_CHAIN, operation, ruleNode.getRuleChainId(), ruleChain);
                    } catch (VizzionnaireException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(ruleNode);
                }
            }), executor);
        }
    }

    private void validateCustomer(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<Customer> customerFuture = customerService.findCustomerByIdAsync(currentUser.getTenantId(), new CustomerId(entityId.getId()));
            Futures.addCallback(customerFuture, getCallback(callback, customer -> {
                if (customer == null) {
                    return ValidationResult.entityNotFound("Customer with requested id wasn't found!");
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.CUSTOMER, operation, entityId, customer);
                    } catch (VizzionnaireException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(customer);
                }
            }), executor);
        }
    }

    private void validateTenant(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isCustomerUser()) {
            callback.onSuccess(ValidationResult.accessDenied(CUSTOMER_USER_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.ok(null));
        } else {
            ListenableFuture<Tenant> tenantFuture = tenantService.findTenantByIdAsync(currentUser.getTenantId(), TenantId.fromUUID(entityId.getId()));
            Futures.addCallback(tenantFuture, getCallback(callback, tenant -> {
                if (tenant == null) {
                    return ValidationResult.entityNotFound("Tenant with requested id wasn't found!");
                }
                try {
                    accessControlService.checkPermission(currentUser, Resource.TENANT, operation, entityId, tenant);
                } catch (VizzionnaireException e) {
                    return ValidationResult.accessDenied(e.getMessage());
                }
                return ValidationResult.ok(tenant);

            }), executor);
        }
    }

    private void validateTenantProfile(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.ok(null));
        } else {
            callback.onSuccess(ValidationResult.accessDenied(ONLY_SYSTEM_ADMINISTRATOR_IS_ALLOWED_TO_PERFORM_THIS_OPERATION));
        }
    }

    private void validateUser(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        ListenableFuture<User> userFuture = userService.findUserByIdAsync(currentUser.getTenantId(), new UserId(entityId.getId()));
        Futures.addCallback(userFuture, getCallback(callback, user -> {
            if (user == null) {
                return ValidationResult.entityNotFound("User with requested id wasn't found!");
            }
            try {
                accessControlService.checkPermission(currentUser, Resource.USER, operation, entityId, user);
            } catch (VizzionnaireException e) {
                return ValidationResult.accessDenied(e.getMessage());
            }
            return ValidationResult.ok(user);

        }), executor);
    }

    private void validateEntityView(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<EntityView> entityViewFuture = entityViewService.findEntityViewByIdAsync(currentUser.getTenantId(), new EntityViewId(entityId.getId()));
            Futures.addCallback(entityViewFuture, getCallback(callback, entityView -> {
                if (entityView == null) {
                    return ValidationResult.entityNotFound(ENTITY_VIEW_WITH_REQUESTED_ID_NOT_FOUND);
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.ENTITY_VIEW, operation, entityId, entityView);
                    } catch (VizzionnaireException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(entityView);
                }
            }), executor);
        }
    }

    private void validateEdge(final SecurityUser currentUser, Operation operation, EntityId entityId, FutureCallback<ValidationResult> callback) {
        if (currentUser.isSystemAdmin()) {
            callback.onSuccess(ValidationResult.accessDenied(SYSTEM_ADMINISTRATOR_IS_NOT_ALLOWED_TO_PERFORM_THIS_OPERATION));
        } else {
            ListenableFuture<Edge> edgeFuture = edgeService.findEdgeByIdAsync(currentUser.getTenantId(), new EdgeId(entityId.getId()));
            Futures.addCallback(edgeFuture, getCallback(callback, edge -> {
                if (edge == null) {
                    return ValidationResult.entityNotFound(EDGE_WITH_REQUESTED_ID_NOT_FOUND);
                } else {
                    try {
                        accessControlService.checkPermission(currentUser, Resource.EDGE, operation, entityId, edge);
                    } catch (VizzionnaireException e) {
                        return ValidationResult.accessDenied(e.getMessage());
                    }
                    return ValidationResult.ok(edge);
                }
            }), executor);
        }
    }

    private <T, V> FutureCallback<T> getCallback(FutureCallback<ValidationResult> callback, Function<T, ValidationResult<V>> transformer) {
        return new FutureCallback<T>() {
            @Override
            public void onSuccess(@Nullable T result) {
                callback.onSuccess(transformer.apply(result));
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        };
    }

    public static void handleError(Throwable e, final DeferredResult<ResponseEntity> response, HttpStatus defaultErrorStatus) {
        ResponseEntity responseEntity;
        if (e instanceof ToErrorResponseEntity) {
            responseEntity = ((ToErrorResponseEntity) e).toErrorResponseEntity();
        } else if (e instanceof IllegalArgumentException || e instanceof IncorrectParameterException) {
            responseEntity = new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } else {
            responseEntity = new ResponseEntity<>(defaultErrorStatus);
        }
        response.setResult(responseEntity);
    }

    public interface ThreeConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}
