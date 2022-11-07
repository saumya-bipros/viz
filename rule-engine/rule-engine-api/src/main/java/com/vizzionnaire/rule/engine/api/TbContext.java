package com.vizzionnaire.rule.engine.api;

import io.netty.channel.EventLoopGroup;

import com.vizzionnaire.common.util.ListeningExecutor;
import com.vizzionnaire.rule.engine.api.sms.SmsSenderFactory;
import com.vizzionnaire.server.cluster.TbClusterService;
import com.vizzionnaire.server.common.data.Customer;
import com.vizzionnaire.server.common.data.Device;
import com.vizzionnaire.server.common.data.DeviceProfile;
import com.vizzionnaire.server.common.data.TenantProfile;
import com.vizzionnaire.server.common.data.alarm.Alarm;
import com.vizzionnaire.server.common.data.asset.Asset;
import com.vizzionnaire.server.common.data.id.CustomerId;
import com.vizzionnaire.server.common.data.id.DeviceId;
import com.vizzionnaire.server.common.data.id.EdgeId;
import com.vizzionnaire.server.common.data.id.EntityId;
import com.vizzionnaire.server.common.data.id.QueueId;
import com.vizzionnaire.server.common.data.id.RuleChainId;
import com.vizzionnaire.server.common.data.id.RuleNodeId;
import com.vizzionnaire.server.common.data.id.TenantId;
import com.vizzionnaire.server.common.data.page.PageData;
import com.vizzionnaire.server.common.data.page.PageLink;
import com.vizzionnaire.server.common.data.rule.RuleNode;
import com.vizzionnaire.server.common.data.rule.RuleNodeState;
import com.vizzionnaire.server.common.msg.TbMsg;
import com.vizzionnaire.server.common.msg.TbMsgMetaData;
import com.vizzionnaire.server.dao.asset.AssetService;
import com.vizzionnaire.server.dao.attributes.AttributesService;
import com.vizzionnaire.server.dao.cassandra.CassandraCluster;
import com.vizzionnaire.server.dao.customer.CustomerService;
import com.vizzionnaire.server.dao.dashboard.DashboardService;
import com.vizzionnaire.server.dao.device.DeviceService;
import com.vizzionnaire.server.dao.edge.EdgeEventService;
import com.vizzionnaire.server.dao.edge.EdgeService;
import com.vizzionnaire.server.dao.entityview.EntityViewService;
import com.vizzionnaire.server.dao.nosql.CassandraStatementTask;
import com.vizzionnaire.server.dao.nosql.TbResultSetFuture;
import com.vizzionnaire.server.dao.ota.OtaPackageService;
import com.vizzionnaire.server.dao.queue.QueueService;
import com.vizzionnaire.server.dao.relation.RelationService;
import com.vizzionnaire.server.dao.resource.ResourceService;
import com.vizzionnaire.server.dao.rule.RuleChainService;
import com.vizzionnaire.server.dao.tenant.TenantService;
import com.vizzionnaire.server.dao.timeseries.TimeseriesService;
import com.vizzionnaire.server.dao.user.UserService;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by ashvayka on 13.01.18.
 */
public interface TbContext {

    /*
     *
     *  METHODS TO CONTROL THE MESSAGE FLOW
     *
     */

    /**
     * Indicates that message was successfully processed by the rule node.
     * Sends message to all Rule Nodes in the Rule Chain
     * that are connected to the current Rule Node using "Success" relationType.
     *
     * @param msg
     */
    void tellSuccess(TbMsg msg);

    /**
     * Sends message to all Rule Nodes in the Rule Chain
     * that are connected to the current Rule Node using specified relationType.
     *
     * @param msg
     * @param relationType
     */
    void tellNext(TbMsg msg, String relationType);

    /**
     * Sends message to all Rule Nodes in the Rule Chain
     * that are connected to the current Rule Node using one of specified relationTypes.
     *
     * @param msg
     * @param relationTypes
     */
    void tellNext(TbMsg msg, Set<String> relationTypes);

    /**
     * Sends message to the current Rule Node with specified delay in milliseconds.
     * Note: this message is not queued and may be lost in case of a server restart.
     *
     * @param msg
     */
    void tellSelf(TbMsg msg, long delayMs);

    /**
     * Notifies Rule Engine about failure to process current message.
     *
     * @param msg - message
     * @param th  - exception
     */
    void tellFailure(TbMsg msg, Throwable th);

    /**
     * Puts new message to queue for processing by the Root Rule Chain
     *
     * @param msg - message
     */
    void enqueue(TbMsg msg, Runnable onSuccess, Consumer<Throwable> onFailure);

    /**
     * Sends message to the nested rule chain.
     * Fails processing of the message if the nested rule chain is not found.
     *
     * @param msg - the message
     * @param ruleChainId - the id of a nested rule chain
     */
    void input(TbMsg msg, RuleChainId ruleChainId);

    /**
     * Sends message to the caller rule chain.
     * Acknowledge the message if no caller rule chain is present in processing stack
     *
     * @param msg - the message
     * @param relationType - the relation type that will be used to route messages in the caller rule chain
     */
    void output(TbMsg msg, String relationType);

    /**
     * Puts new message to custom queue for processing
     *
     * @param msg - message
     */
    void enqueue(TbMsg msg, String queueName, Runnable onSuccess, Consumer<Throwable> onFailure);

    void enqueueForTellFailure(TbMsg msg, String failureMessage);

    void enqueueForTellNext(TbMsg msg, String relationType);

    void enqueueForTellNext(TbMsg msg, Set<String> relationTypes);

    void enqueueForTellNext(TbMsg msg, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure);

    void enqueueForTellNext(TbMsg msg, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure);

    void enqueueForTellNext(TbMsg msg, String queueName, String relationType, Runnable onSuccess, Consumer<Throwable> onFailure);

    void enqueueForTellNext(TbMsg msg, String queueName, Set<String> relationTypes, Runnable onSuccess, Consumer<Throwable> onFailure);

    void ack(TbMsg tbMsg);

    TbMsg newMsg(String queueName, String type, EntityId originator, TbMsgMetaData metaData, String data);

    TbMsg newMsg(String queueName, String type, EntityId originator, CustomerId customerId, TbMsgMetaData metaData, String data);

    TbMsg transformMsg(TbMsg origMsg, String type, EntityId originator, TbMsgMetaData metaData, String data);

    TbMsg customerCreatedMsg(Customer customer, RuleNodeId ruleNodeId);

    TbMsg deviceCreatedMsg(Device device, RuleNodeId ruleNodeId);

    TbMsg assetCreatedMsg(Asset asset, RuleNodeId ruleNodeId);

    // TODO: Does this changes the message?
    TbMsg alarmActionMsg(Alarm alarm, RuleNodeId ruleNodeId, String action);

    void onEdgeEventUpdate(TenantId tenantId, EdgeId edgeId);

    /*
     *
     *  METHODS TO PROCESS THE MESSAGES
     *
     */

    boolean isLocalEntity(EntityId entityId);

    RuleNodeId getSelfId();

    RuleNode getSelf();

    String getRuleChainName();

    TenantId getTenantId();

    AttributesService getAttributesService();

    CustomerService getCustomerService();

    TenantService getTenantService();

    UserService getUserService();

    AssetService getAssetService();

    DeviceService getDeviceService();

    TbClusterService getClusterService();

    DashboardService getDashboardService();

    RuleEngineAlarmService getAlarmService();

    RuleChainService getRuleChainService();

    RuleEngineRpcService getRpcService();

    RuleEngineTelemetryService getTelemetryService();

    TimeseriesService getTimeseriesService();

    RelationService getRelationService();

    EntityViewService getEntityViewService();

    ResourceService getResourceService();

    OtaPackageService getOtaPackageService();

    RuleEngineDeviceProfileCache getDeviceProfileCache();

    EdgeService getEdgeService();

    EdgeEventService getEdgeEventService();

    QueueService getQueueService();

    ListeningExecutor getMailExecutor();

    ListeningExecutor getSmsExecutor();

    ListeningExecutor getDbCallbackExecutor();

    ListeningExecutor getExternalCallExecutor();

    MailService getMailService(boolean isSystem);

    SmsService getSmsService();

    SmsSenderFactory getSmsSenderFactory();

    ScriptEngine createJsScriptEngine(String script, String... argNames);

    void logJsEvalRequest();

    void logJsEvalResponse();

    void logJsEvalFailure();

    String getServiceId();

    EventLoopGroup getSharedEventLoop();

    CassandraCluster getCassandraCluster();

    TbResultSetFuture submitCassandraReadTask(CassandraStatementTask task);

    TbResultSetFuture submitCassandraWriteTask(CassandraStatementTask task);

    PageData<RuleNodeState> findRuleNodeStates(PageLink pageLink);

    RuleNodeState findRuleNodeStateForEntity(EntityId entityId);

    void removeRuleNodeStateForEntity(EntityId entityId);

    RuleNodeState saveRuleNodeState(RuleNodeState state);

    void clearRuleNodeStates();

    void addTenantProfileListener(Consumer<TenantProfile> listener);

    void addDeviceProfileListeners(Consumer<DeviceProfile> listener, BiConsumer<DeviceId, DeviceProfile> deviceListener);

    void removeListeners();

    TenantProfile getTenantProfile();
}
