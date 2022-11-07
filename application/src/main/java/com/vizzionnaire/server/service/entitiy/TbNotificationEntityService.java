package com.vizzionnaire.server.service.entitiy;

import java.util.List;

import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.HasName;
import com.vizzionnaire.server.common.data.Tenant;
import com.vizzionnaire.server.common.data.User;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.audit.ActionType;
import com.vizzionnaire.server.common.data.edge.Edge;
import com.vizzionnaire.server.common.data.edge.EdgeEventActionType;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.plugin.ComponentLifecycleEvent;
import com.vizzionnaire.server.common.data.relation.EntityRelation;
import com.vizzionnaire.server.common.data.rule.RuleChain;
import com.vizzionnaire.server.common.data.security.DeviceCredentials;

public interface TbNotificationEntityService {

    <I extends EntityId> void logEntityAction(TenantId tenantId, I entityId, ActionType actionType, User user,
                                              Exception e, Object... additionalInfo);

    <E extends HasName, I extends EntityId> void logEntityAction(TenantId tenantId, I entityId, E entity, ActionType actionType,
                                                                 User user, Object... additionalInfo);

    <E extends HasName, I extends EntityId> void logEntityAction(TenantId tenantId, I entityId, E entity, ActionType actionType,
                                                                 User user, Exception e, Object... additionalInfo);

    <E extends HasName, I extends EntityId> void logEntityAction(TenantId tenantId, I entityId, E entity, CustomerId customerId,
                                                                 ActionType actionType, User user, Object... additionalInfo);

    <E extends HasName, I extends EntityId> void logEntityAction(TenantId tenantId, I entityId, E entity, CustomerId customerId,
                                                                 ActionType actionType, User user, Exception e,
                                                                 Object... additionalInfo);

    <E extends HasName, I extends EntityId> void notifyCreateOrUpdateEntity(TenantId tenantId, I entityId, E entity,
                                                                            CustomerId customerId, ActionType actionType,
                                                                            User user, Object... additionalInfo);

    <E extends HasName, I extends EntityId> void notifyDeleteEntity(TenantId tenantId, I entityId, E entity,
                                                                    CustomerId customerId, ActionType actionType,
                                                                    List<EdgeId> relatedEdgeIds,
                                                                    User user, Object... additionalInfo);

    void notifyDeleteAlarm(TenantId tenantId, Alarm alarm, EntityId originatorId, CustomerId customerId,
                           List<EdgeId> relatedEdgeIds, User user, String body, Object... additionalInfo);

    void notifyDeleteRuleChain(TenantId tenantId, RuleChain ruleChain,
                               List<EdgeId> relatedEdgeIds, User user);

    <I extends EntityId> void notifySendMsgToEdgeService(TenantId tenantId, I entityId, EdgeEventActionType edgeEventActionType);

    <E extends HasName, I extends EntityId> void notifyAssignOrUnassignEntityToCustomer(TenantId tenantId, I entityId,
                                                                                        CustomerId customerId, E entity,
                                                                                        ActionType actionType,
                                                                                        User user, boolean sendToEdge,
                                                                                        Object... additionalInfo);

    <E extends HasName, I extends EntityId> void notifyAssignOrUnassignEntityToEdge(TenantId tenantId, I entityId,
                                                                                    CustomerId customerId, EdgeId edgeId,
                                                                                    E entity, ActionType actionType,
                                                                                    User user, Object... additionalInfo);

    void notifyCreateOrUpdateTenant(Tenant tenant, ComponentLifecycleEvent event);

    void notifyDeleteTenant(Tenant tenant);

    void notifyCreateOrUpdateDevice(TenantId tenantId, DeviceId deviceId, CustomerId customerId, Device device,
                                    Device oldDevice, ActionType actionType, User user, Object... additionalInfo);

    void notifyDeleteDevice(TenantId tenantId, DeviceId deviceId, CustomerId customerId, Device device,
                            List<EdgeId> relatedEdgeIds, User user, Object... additionalInfo);

    void notifyUpdateDeviceCredentials(TenantId tenantId, DeviceId deviceId, CustomerId customerId, Device device,
                                       DeviceCredentials deviceCredentials, User user);

    void notifyAssignDeviceToTenant(TenantId tenantId, TenantId newTenantId, DeviceId deviceId, CustomerId customerId,
                                    Device device, Tenant tenant, User user, Object... additionalInfo);

    void notifyEdge(TenantId tenantId, EdgeId edgeId, CustomerId customerId, Edge edge, ActionType actionType,
                    User user, Object... additionalInfo);

    void notifyCreateOrUpdateAlarm(Alarm alarm, ActionType actionType, User user, Object... additionalInfo);

    <E extends HasName, I extends EntityId> void notifyCreateOrUpdateOrDelete(TenantId tenantId, CustomerId customerId,
                                                                              I entityId, E entity, User user,
                                                                              ActionType actionType, boolean sendNotifyMsgToEdge,
                                                                              Exception e, Object... additionalInfo);

    void notifyRelation(TenantId tenantId, CustomerId customerId, EntityRelation relation, User user,
                        ActionType actionType, Object... additionalInfo);
}
